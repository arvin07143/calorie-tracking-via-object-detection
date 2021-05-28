from sqlalchemy import Column, Integer, create_engine, Enum, String, Float, Date, ForeignKey
from sqlalchemy.orm import sessionmaker, relationship, backref
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()


class User(Base):
    __tablename__ = 'users'
    id = Column(String, primary_key=True)
    profile_name = Column(String)
    email = Column(String)

    def __repr__(self):
        return self.id


class Goal(Base):
    __tablename__ = 'goals'
    id = Column(String, primary_key=True)
    goal_name = Column(String)
    goal_type = Column(Enum("Calorie", "Weight", name="goal_enum"))
    goal_value = Column(Float)
    goal_start = Column(Date)
    goal_end = Column(Date)
    user_id = Column(String, ForeignKey(users.id))
    user = relationship(User, backref=backref('users', uselist=True))


def recreate_database():
    Base.metadata.drop_all(engine)
    Base.metadata.create_all(engine)
