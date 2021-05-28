from . import config
from flask import Flask
from flask_migrate import Migrate
from flask_sqlalchemy import SQLAlchemy
from firebase_admin import credentials
import firebase_admin

app = Flask(__name__)
app.config.from_object(config.DevelopmentConfig())
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
cred = credentials.Certificate(app.config['FIREBASE_ADMIN_CRED_PATH'])
if len(firebase_admin._apps) == 0:
    firebase_admin.initialize_app(cred)
db = SQLAlchemy(app)
migrate = Migrate(app, db)

from app import routes, models
