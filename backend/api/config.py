import os


class Config(object):
    DEBUG = False
    TESTING = False
    DB_SERVER = ''
    USER_NAME = ''
    USER_PASSWORD = ''
    DB_NAME = 'mydb'
    NUTRITIONIX_APP_ID = '0b6fab2a'
    NUTRITIONIX_API_KEY = '5468ce4133bea2bc4c73132bac529470'
    NUTRITIONIX_API_URL = 'https://api.nutritionix.com/v1_1/search'

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
