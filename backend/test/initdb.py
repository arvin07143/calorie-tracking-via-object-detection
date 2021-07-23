import json
import time
from datetime import datetime

from app import db
from app import models

models.SavedItems.__table__.create(db.engine)

db.session.commit()
