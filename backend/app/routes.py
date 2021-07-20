import datetime

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


@app.route("/detection/predict/", methods=['GET', 'POST'])
def inference():
    if 'file' not in request.files:
        abort(500)

    uploaded_file = request.files['file']
    received_image = Image.open(uploaded_file)

    detector = FoodImageDetection(received_image)
    results = detector.get_results()

    print(results)

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
        date = data['dob'].split('T')[0]
        date_of_birth = datetime.datetime.strptime(date, "%Y-%m-%d")
        new_user = models.User(uid=uid, gender=models.GenderEnum(gender), height=height, weight=weight,
                               date_of_birth=date_of_birth)
        db.session.add(new_user)
        db.session.commit()
        return "User added successfully", 200


@app.route("/users/<uid>", methods=['PUT'])
def update_user(uid):
    try:
        uid = auth_api_key(request.headers['Authorization'])
    except KeyError:
        abort(401)

    if uid is not None:
        current_user = models.User.query.get_or_404(uid)
        data = request.json

        print(data)

        if "gender" in data:
            gender = data['gender']
            current_user.gender = gender

        if "height" in data:
            height = data['height']
            current_user.height = height

        if "weight" in data:
            weight = data['weight']
            current_user.weight = weight

        if "date" in data:
            date = data['dob'].split('T')[0]
            date_of_birth = datetime.datetime.strptime(date, "%Y-%m-%d")
            current_user.date_of_birth = date_of_birth

        db.session.commit()

    return "User Update Successfully", 200


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

    except KeyError:
        abort(401)

    meal = models.Meal.query.order_by(desc(models.Meal.meal_time)).filter_by(user_id=uid).first()
    return jsonify(meal_id=meal.id), 200


@app.route("/users/<uid>/goals/", methods=["POST"])
def add_new_goal(uid):
    try:
        uid = auth_api_key(request.headers['Authorization'])
        data = request.json
        goalType = data["goal_type"]
        goalStartValue = data["goal_start"]
        goalEndValue = data["goal_end"]

        newGoal = models.Goal(goal_type=models.GoalEnum(goalType), goal_start_value=goalStartValue,
                              goal_end_value=goalEndValue)
        user = models.User.query.get_or_404(uid)

        user.goals.append(newGoal)
        db.session.commit()
        goalID = newGoal.id

    except KeyError as e:
        print(e)
        abort(401)

    return jsonify(goal_id=goalID), 200


@app.route("/users/<uid>/goals/<goal_id>", methods=["PUT"])
def update_goal(uid, goal_id):
    try:
        uid = auth_api_key(request.headers['Authorization'])
        data = request.json
        goalType = data["goal_type"]
        goalStartValue = data["goal_start"]
        goalEndValue = data["goal_end"]

        changedGoal = models.Goal.query.get_or_404(goal_id)

        changedGoal.goal_start_value = goalStartValue
        changedGoal.goal_end_value = goalEndValue

        db.session.commit()
        goalID = changedGoal.id

    except KeyError as e:
        print(e)
        abort(401)

    return jsonify(goal_id=goalID), 200


@app.route("/users/<uid>/saved/", methods=["POST"])
def add_new_saved_item(uid):
    try:
        uid = auth_api_key(request.headers['Authorization'])
        data = request.json
        meal_name = data["item_name"]
        meal_calorie = data["calories"]

        new_saved_item = models.SavedItems(item_name=meal_name, item_calorie=meal_calorie)
        user = models.User.query.get_or_404(uid)

        user.saved_items.append(new_saved_item)
        db.session.commit()
        item_id = new_saved_item.id

    except KeyError as e:
        print(e)
        abort(401)

    return jsonify(item_id=item_id), 200


@app.route("/users/<uid>/goals/", methods=["GET"])
def get_all_goals(uid):
    try:
        uid = auth_api_key(request.headers['Authorization'])
        user = models.User.query.get_or_404(uid)
        goals = models.Goal.query.with_parent(user).all()

        output = []
        for goal in goals:
            json = dict()
            json["goal_id"] = goal.id
            json["goal_type"] = goal.goal_type
            json["goal_start"] = goal.goal_start_value
            json["goal_end"] = goal.goal_end_value
            output.append(json)

    except KeyError as e:
        print(e)
        abort(401)

    return jsonify(output), 200


@app.route("/users/<uid>/saved/", methods=["GET"])
def get_all_saved_items(uid):
    try:
        uid = auth_api_key(request.headers['Authorization'])
        user = models.User.query.get_or_404(uid)
        saved_items = models.SavedItems.query.with_parent(user).all()

        output = []
        for item in saved_items:
            json = dict()
            json["saved_id"] = item.id
            json["item_name"] = item.item_name
            json["calories"] = item.item_calorie
            output.append(json)

    except KeyError as e:
        print(e)
        abort(401)

    return jsonify(output), 200


@app.route("/nutrition/search/<search_term>", methods=['GET'])
def search_nutrition(search_term):
    caloric_estimator = CalorieEstimation()
    data = caloric_estimator.item_search(search_term)
    return data


@app.route("/nutrition/calories", methods=['GET'])
def get_caloric_content():
    try:
        data = request.json
        food_name = data['food-name']
    except (TypeError, BadRequest, KeyError) as ex:
        food_name = request.args['food-name']

    estimator = CalorieEstimation()
    calorie_list = estimator.get_calories_from_list(food_list=[food_name])
    total = estimator.get_total_calories(calorie_list)
    return jsonify(calorie_list=calorie_list, total_calories=total)


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

    def get_calories_from_list(self, food_list):
        name_list = []
        calorie_list = []
        for item in food_list:
            self.api_params['query'] = item
            r = requests.post(url=self.url, data=self.api_params)
            data = r.json()
            if int(data['total']) == 0:
                calorie_list.append(0)
            else:
                name_list.append((data['hits'][0]['fields']['item_name']))
                calorie_list.append(int(data['hits'][0]['fields']['nf_calories']))

        return name_list, calorie_list

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

    @staticmethod
    def get_total_calories(calorie_list):
        args = [c for c in calorie_list if c is not None]
        return sum(args) if args else 0


class FoodImageDetection:
    OBJECT_DETECTION_URL = app.config['TF_SERVER_URL']
    PATH_TO_LABELS = app.config['PATH_LABELS']

    def __init__(self, image):
        self.image_array = np.array(image.convert('RGB'))
        payload = {
            "instances": [self.image_array.tolist()]
        }

        self.output = dict()
        r = requests.post(self.OBJECT_DETECTION_URL, json=payload)
        pred = (r.json())["predictions"][0]
        detected_items_index = [i for i in range(len(pred['detection_scores'])) if pred['detection_scores'][i] > 0.5]

        category_index = create_category_index_from_labelmap(self.PATH_TO_LABELS,
                                                             use_display_name=True)

        out = dict()
        for i in detected_items_index:
            out['detection_box'] = out.get('detection_box', []) + pred['detection_boxes'][i]
            out['detection_class'] = out.get('detection_class', "") + category_index[pred['detection_classes'][i]][
                'name']
            out['detection_score'] = out.get('detection_score', 0) + pred['detection_scores'][i]
            self.output["predictions"] = self.output.get("predictions", []) + [out]

    def get_results(self):

        food_list = [x["detection_class"] for x in self.output.get("predictions", [])]

        name, calorie = CalorieEstimation().get_calories_from_list(food_list)

        for idx, val in enumerate(calorie):
            self.output["predictions"][idx]['detection_class'] = name[idx]
            self.output["predictions"][idx]["calories"] = val

        self.output["total_calories"] = CalorieEstimation().get_total_calories(calorie)

        return self.output


class BarcodeDetection:
    BARCODE_LOOKUP_API_ENDPOINT = "https://api.upcitemdb.com/prod/trial/lookup"

    def __init__(self, barcode_string):
        self.barcode = barcode_string
