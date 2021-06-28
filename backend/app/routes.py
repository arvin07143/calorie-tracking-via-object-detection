import base64
import datetime
from io import BytesIO

import numpy as np
import requests
from PIL import Image
from firebase_admin import auth
from flask import jsonify
from flask_sqlalchemy import *
from object_detection.utils.label_map_util import create_category_index_from_labelmap
from sqlalchemy import desc
from werkzeug.exceptions import BadRequest

from . import app, models, db

app.json_encoder = models.CustomJSONEncoder


@app.route("/")
def hello_world():
    return "Hello World"


@app.route("/detection/predict/", methods=['GET'])
# TODO CHANGE TO GET ONLY
def inference():
    try:
        data = request.json
        image_b64 = data['b64']
    except (TypeError, BadRequest, KeyError):
        image_b64 = request.args["b64"]

    received_image = Image.open(BytesIO(base64.b64decode(image_b64)))

    detector = FoodImageDetection(received_image)
    results = detector.get_results()

    return jsonify(results)


@app.route("/users/", methods=['POST'])
def register_new_user():
    try:
        uid = auth_api_key(request.headers['Authorization'])
    except KeyError:
        abort(401)
    if uid is not None:
        data = request.json
        gender = data['gender']
        height = data['height']
        weight = data['weight']
        date_of_birth = datetime.datetime.strptime(data['dob'], "%Y-%m-%d")
        new_user = models.User(uid, models.GenderEnum(gender), height, weight, date_of_birth)
        db.session.add(new_user)
        db.session.commit()
        return "User added successfully", 200


@app.route("/users/<uid>", methods=['GET'])
def get_user_data(uid):
    try:
        auth_uid = auth_api_key(request.headers['Authorization'])
        user = None
        if uid == "me":
            user = models.User.query.get(auth_uid)
        elif uid == auth_uid:
            user = models.User.query.get(uid)

        if user is not None:
            print(type(user.date_of_birth))
            return jsonify(user)
        else:
            abort(404)
    except KeyError:
        abort(401)


@app.route("/users/<uid>/meals/", methods=['GET'])
def get_meal_date(uid):
    meals = models.Meal.query.all()
    output = dict()
    output["user_id"] = uid
    output_meal_list = list()
    for meal in meals:
        meal_details = dict()
        meal_details["meal_id"] = meal.id
        meal_details["meal_time"] = meal.meal_time
        meal_details["meal_type"] = meal.meal_type
        meal_details["meal_content"] = list()
        meal_items = models.MealItem.query.filter_by(meal_id=meal.id).all()
        for item in meal_items:
            meal_dict = dict()
            meal_dict["item_name"] = item.item_name
            meal_dict["calories"] = item.item_calorie
            meal_details["meal_content"].append(meal_dict)

        output_meal_list.append(meal_details)
    output["meals"] = output_meal_list

    return output


@app.route("/users/<uid>/<meal_id>/", methods=['POST'])
def add_meal_item(uid, meal_id):
    meals = models.Meal.query.get_or_404(meal_id)
    try:
        data = request.json
        print(data)
        if type(data) is str:
            data = list(data)

        for item in data:
            item_name = item["item_name"]
            item_calorie = item["calories"]
            new_meal_item = models.MealItem(item_name=item_name, item_calorie=item_calorie)
            meals.meal_items.append(new_meal_item)
        db.session.commit()

        return jsonify(status="success"), 200

    except Exception as e:
        print(e)

    return jsonify(status="fail"), 500


@app.route("/users/<uid>/meals/", methods=['POST'])
def add_new_meal(uid):
    try:
        uid = auth_api_key(request.headers['Authorization'])
        data = request.json
        meal_content = data["meal_content"]
        meal_time = data["meal_time"]
        meal_type = data["meal_type"]
        new_meal = models.Meal(user_id=uid, meal_type=models.MealTypeEnum(meal_type),
                               meal_time=meal_time)
        db.session.add(new_meal)
        db.session.commit()

        meal = models.Meal.query.order_by(desc(models.Meal.meal_time)).filter_by(user_id=uid).first()

    except KeyError:
        abort(401)

    return jsonify(meal_id=meal.id), 200


@app.route("/nutrition/search/<search_term>", methods=['GET'])
def search_nutrition(search_term):
    caloric_estimator = CalorieEstimation([])
    data = caloric_estimator.item_search(search_term)
    print(data)
    return data


@app.route("/nutrition/calories", methods=['GET'])
def get_caloric_content():
    try:
        data = request.json
        food_name = data['food-name']
    except (TypeError, BadRequest, KeyError) as ex:
        food_name = request.args['food-name']

    estimator = CalorieEstimation(food_name)
    return jsonify(calorie_list=estimator.calorie_list, total_calories=estimator.get_total_calories())


def auth_api_key(id_token):
    decoded_token = auth.verify_id_token(id_token)
    uid = decoded_token['uid']
    if uid is not None:
        return uid
    else:
        return None


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


class CalorieEstimation:
    url = app.config['NUTRITIONIX_API_URL']
    api_params = {
        "appId": app.config['NUTRITIONIX_APP_ID'],
        "appKey": app.config['NUTRITIONIX_API_KEY'],
        "query": "",
        "fields": ['item_name', 'nf_calories']
    }

    calorie_list = []

    def __init__(self, food_list):
        self.food_list = food_list

    def get_calories_from_list(self):
        for item in self.food_list:
            self.api_params['query'] = item
            r = requests.post(url=self.url, data=self.api_params)
            data = r.json()
            print(data)
            if int(data['total']) == 0:
                self.calorie_list.append(None)
            else:
                self.calorie_list.append(int(data['hits'][0]['fields']['nf_calories']))

    def item_search(self, search_term):
        output = dict()
        self.api_params['query'] = search_term
        r = requests.post(url=self.url, data=self.api_params)
        data = r.json()

        output["total"] = int(data['total'])
        result_list = list()
        for result in data["hits"]:
            result_list.append(result["fields"])

        output["results"] = result_list

        return output

    def get_total_calories(self):
        args = [c for c in self.calorie_list if c is not None]
        return sum(args) if args else 0


class FoodImageDetection:
    OBJECT_DETECTION_URL = app.config['TF_SERVER_URL']
    PATH_TO_LABELS = app.config['PATH_LABELS']

    output = dict()

    def __init__(self, image):
        self.image_array = np.array(image.convert('RGB'))
        payload = {
            "instances": [self.image_array.tolist()]
        }

        r = requests.post(self.OBJECT_DETECTION_URL, json=payload)
        pred = (r.json())["predictions"][0]
        detected_items_index = [i for i in range(len(pred['detection_scores'])) if pred['detection_scores'][i] > 0.5]

        category_index = create_category_index_from_labelmap(self.PATH_TO_LABELS,
                                                             use_display_name=True)

        for i in detected_items_index:
            out = dict()
            out['detection_box'] = out.get('detection_box', []) + pred['detection_boxes'][i]
            out['detection_class'] = out.get('detection_class', "") + category_index[pred['detection_classes'][i]][
                'name']
            out['detection_score'] = out.get('detection_score', 0) + pred['detection_scores'][i]
            self.output["predictions"] = self.output.get("predictions", []) + [out]

    def get_results(self):
        estimator = CalorieEstimation(self.output)
        for item in estimator.calorie_list:
            self.output["predictions"][estimator.calorie_list.index(item)]["calories"] = item

        self.output["total_calories"] = estimator.get_total_calories()

        return self.output


class BarcodeDetection:
    BARCODE_LOOKUP_API_ENDPOINT = "https://api.upcitemdb.com/prod/trial/lookup"

    def __init__(self, barcode_string):
        self.barcode = barcode_string
