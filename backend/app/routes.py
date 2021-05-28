import base64
import json
import os
from io import BytesIO

import numpy as np
import requests
from PIL import Image
from firebase_admin import auth
from flask import Flask, request, jsonify
from object_detection.utils.label_map_util import create_category_index_from_labelmap

from app import app


@app.route("/")
def hello_world():
    return "Hello World"


@app.route("/detection/predict/", methods=['GET', 'POST'])
def inference():
    PATH_TO_LABELS = "label_map.pbtxt"
    image_b64 = ""
    if not auth_api_key(request.headers['Authorization']):
        raise handle_invalid_usage('Invalid JWT', error=401)
    try:
        data = request.json
        image_b64 = data['b64']
    except (TypeError, BadRequest, KeyError):
        image_b64 = request.args["b64"]

    received_image = Image.open(BytesIO(base64.b64decode(image_b64)))
    np_img = np.array(received_image.convert('RGB'))

    payload = {
        "instances": [np_img.tolist()]
    }

    r = requests.post("http://localhost:8501/v1/models/my_model:predict", json=payload)
    pred = (r.json())["predictions"][0]
    detected_items_index = [i for i in range(len(pred['detection_scores'])) if pred['detection_scores'][i] > 0.5]

    output_data = dict()
    category_index = create_category_index_from_labelmap(PATH_TO_LABELS,
                                                         use_display_name=True)

    for i in detected_items_index:
        out = dict()
        out['detection_box'] = out.get('detection_box', []) + pred['detection_boxes'][i]
        out['detection_class'] = out.get('detection_class', "") + category_index[pred['detection_classes'][i]]['name']
        out['detection_score'] = out.get('detection_score', 0) + pred['detection_scores'][i]
        output_data["predictions"] = output_data.get("predictions", []) + [out]

    return output_data


@app.route("/nutrition/calories", methods=['GET'])
def get_caloric_content():
    try:
        data = request.json
        food_name = data['food-name']
    except (TypeError, BadRequest, KeyError) as ex:
        food_name = request.args['food-name']

    print(request.headers)
    url = app.config['NUTRITIONIX_API_URL']
    api_params = {
        "appId": app.config['NUTRITIONIX_APP_ID'],
        "appKey": app.config['NUTRITIONIX_API_KEY'],
        "query": food_name,
        "fields": ['item_name', 'nf_calories']
    }

    print(json.dumps(api_params, indent=2))

    r = requests.post(url=url, data=api_params)
    calorie_data = r.json()

    if int(calorie_data['total']) == 0:
        return "Not found"

    return calorie_data['hits'][0]['fields']  # we are only using the 1st element


def auth_api_key(id_token):
    decoded_token = auth.verify_id_token(id_token)
    uid = decoded_token['uid']
    if uid is not None:
        print(uid)
        return True
    else:
        return False


# @app.errorhandler(InvalidUsage)
# def handle_invalid_usage(error):
#     response = jsonify(error.to_dict())
#     response.status_code = error.status_code
#     return response

@app.errorhandler(auth.InvalidIdTokenError)
def handle_bad_request(e):
    return 'Invalid Token!', 401


if __name__ == "__main__":
    app.run(host="0.0.0.0")
