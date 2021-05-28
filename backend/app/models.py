from . import db


class User(db.Model):
    __tablename__ = 'users'
    id = db.Column(db.String, primary_key=True)
    profile_name = db.Column(db.String)
    email = db.Column(db.String)

    def __repr__(self):
        return self.id


class Goal(db.Model):
    __tablename__ = 'goals'
    id = db.Column(db.String, primary_key=True)
    goal_name = db.Column(db.String)
    goal_type = db.Column(db.Enum("Calorie", "Weight", name="goal_enum"))
    goal_value = db.Column(db.Float)
    goal_start = db.Column(db.Date)
    goal_end = db.Column(db.Date)
    user_id = db.Column(db.String, db.ForeignKey('users.id'))


def recreate_database():
    Base.metadata.drop_all(engine)
    Base.metadata.create_all(engine)
