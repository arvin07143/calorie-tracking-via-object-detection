import dataclasses
import datetime
import json
from datetime import time
from flask.json import JSONEncoder

from . import db
import enum
import dataclasses


class GenderEnum(enum.IntEnum):
    MALE = 0
    FEMALE = 1
    UNDEFINED = 2


@dataclasses.dataclass
class User(db.Model):
    uid: str
    gender: enum
    height: int
    weight: float
    date_of_birth: datetime.datetime

    __tablename__ = 'users'
    uid = db.Column(db.String, primary_key=True)
    gender = db.Column(db.Enum(GenderEnum))
    height = db.Column(db.Integer)
    weight = db.Column(db.Float)
    date_of_birth = db.Column(db.DateTime)
    goals = db.relationship("Goal")
    meals = db.relationship("Meal")

    # def __init__(self, uid, gender, height, weight, dob):
    #     self.uid = uid
    #     self.gender = gender
    #     self.height = height
    #     self.weight = weight
    #     self.date_of_birth = dob


class GoalEnum(enum.IntEnum):
    WEIGHT_GOAL = 0
    CALORIE_GOAL = 1


class Goal(db.Model):
    __tablename__ = 'goals'
    id = db.Column(db.String, primary_key=True, autoincrement=True)
    goal_name = db.Column(db.String)
    goal_type = db.Column(db.Enum(GoalEnum))
    goal_value = db.Column(db.Float)
    goal_start = db.Column(db.DateTime)
    goal_end = db.Column(db.DateTime)
    user_id = db.Column(db.String, db.ForeignKey('users.uid'))


class MealTypeEnum(enum.IntEnum):
    BREAKFAST = 0
    LUNCH = 1
    DINNER = 2


class CustomJSONEncoder(JSONEncoder):
    "Add support for serializing timedeltas"

    def default(self, o):
        if type(o) == datetime.timedelta:
            return str(o)
        elif type(o) == datetime.datetime:
            return o.astimezone().isoformat(timespec='seconds')
        else:
            return super().default(o)


@dataclasses.dataclass
class Meal(db.Model):
    id: int
    meal_content: json
    user_id: str
    meal_type: enum
    meal_time: datetime.datetime

    __tablename__ = 'meals'
    id = db.Column(db.Integer, primary_key=True, autoincrement=True)
    meal_content = db.Column(db.JSON)
    meal_type = db.Column(db.Enum(MealTypeEnum))
    meal_time = db.Column(db.DateTime)
    user_id = db.Column(db.String, db.ForeignKey('users.uid'))

    # def __init__(self, meal_content, uid):
    #     self.meal_content = meal_content
    #     self.user_id = uid

    # def __repr__(self):
    #     return "ID : " + self.id + "\nContent : " + json.dumps(self.meal_content) + "\nUID : " + self.user_id

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__,
                          sort_keys=True, indent=4)
