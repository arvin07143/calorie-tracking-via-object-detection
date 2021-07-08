import os


class Config(object):
    DEBUG = False
    TESTING = False
    DB_SERVER = ''
    USER_NAME = 'arvin07143'
    USER_PASSWORD = 'arvinng07143'
    DB_NAME = 'mydb'
    NUTRITIONIX_APP_ID = '0b6fab2a'
    NUTRITIONIX_API_KEY = '085d644e439a714953ab674a74c83ddc'
    NUTRITIONIX_API_URL = 'https://api.nutritionix.com/v1_1/search'
    FIREBASE_ADMIN_CRED_PATH = 'final-year-project-857e3-firebase-adminsdk-bg3u9-be02fa831b.json'
    TF_SERVER_URL = "http://localhost:8501/v1/models/my_model:predict"
    PATH_LABELS = "label_map.pbtxt"

    @property
    def SQLALCHEMY_DATABASE_URI(self):
        return f"postgresql+psycopg2://{self.USER_NAME}:{self.USER_PASSWORD}@{self.DB_SERVER}:5432/{self.DB_NAME}"


class ProductionConfig(Config):
    pass


class StagingConfig(Config):
    DEBUG = True


class DevelopmentConfig(Config):
    DEBUG = True
    DEVELOPMENT = True
    DB_SERVER = 'localhost'
