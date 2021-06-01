from . import db
import enum


class GenderEnum(enum.IntEnum):
    MALE = 0
    FEMALE = 1
    UNDEFINED = 2


class User(db.Model):
    __tablename__ = 'users'
    uid = db.Column(db.String, primary_key=True)
    gender = db.Column(db.Enum(GenderEnum))
    height = db.Column(db.Integer)
    weight = db.Column(db.Float)
    date_of_birth = db.Column(db.Date)
    goals = db.relationship("Goal")
    meals = db.relationship("Meal")

    def __init__(self, uid, gender, height, weight, dob):
        self.uid = uid
        self.gender = gender
        self.height = height
        self.weight = weight
        self.date_of_birth = dob


class GoalEnum(enum.IntEnum):
    WEIGHT_GOAL = 0
    CALORIE_GOAL = 1


class Goal(db.Model):
    __tablename__ = 'goals'
    id = db.Column(db.String, primary_key=True)
    goal_name = db.Column(db.String)
    goal_type = db.Column(db.Enum(GoalEnum))
    goal_value = db.Column(db.Float)
    goal_start = db.Column(db.Date)
    goal_end = db.Column(db.Date)
    user_id = db.Column(db.String, db.ForeignKey('users.uid'))


class Meal(db.Model):
    __tablename__ = 'meals'
    id = db.Column(db.String, primary_key=True)
    meal_content = db.Column(db.JSON)
    user_id = db.Column(db.String, db.ForeignKey('users.uid'))
