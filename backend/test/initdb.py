import json
import time
from datetime import datetime

from app import db
from app import models

models.MealItem.__table__.drop(db.engine)
models.Meal.__table__.drop(db.engine)
models.Meal.__table__.create(db.engine)
models.MealItem.__table__.create(db.engine)

db.session.commit()
