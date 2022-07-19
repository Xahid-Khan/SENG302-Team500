"""
    This script ensures that users required for testing still exist after deployment.
    If the test users do not exist from this check, then the CI pipeline will fail at this step.
    For now these users have been hardcoded.
"""

import mariadb
import sys
import os

# TODO: Check all these values are correct
DB_USERNAME = "seng302-team500"
DB_PASS = os.environ['DB_PASSWORD']
DB_HOST = "db2.csse.canterbury.ac.nz"
DB_PORT = 9500
DB_DATABASE = "seng302-2022-team500-identityprovider-test"

def exit(error_message):
    """exits the script giving an error this is so that the build can fail"""
    sys.exit(error_message)

class User:
    """
    Simple class for storing the definition of a user (first name, last name, username)
    """
    def __init__(self, first_name, last_name, username):
        self.first_name = first_name
        self.last_name = last_name
        self.username = username
    
    def __str__(self):
        return f"{self.first_name} {self.last_name} ({self.username})"

class Database:
    """
    Class used for handling database transactions, pulling from globals.
    """
    def __init__(self):
        # TODO: Error handling
        # Establish connection with database
        db_conn = mariadb.connect(
            user=DB_USERNAME,
            password=DB_PASS,
            host=DB_HOST,
            port=DB_PORT,
            database=DB_DATABASE
        )
        # Set cursor
        self.cursor = db_conn.cursor()
    
    def check_user_exists(self, user: User) -> bool:
        """Checks if a certain user exists"""
        self.cursor.execute(
            "SELECT EXISTS(SELECT * FROM user_model WHERE first_name=? AND last_name=? AND username=?)",
            (user.first_name, user.last_name, user.username)
        )

        return bool(self.cursor[0])    

def main():
    database = Database()

    # Definitions of users which should always exist in the testing environment
    student = User("John", "Smith", "JohnSmith123")
    teacher = User("Jane", "Doe", "JaneDoeTeacher")
    course_admin = User("Dave", "Rogers", "TheAdministrator")

    error_message = ""
    for user in [student, teacher, course_admin]:
        # If the user does *not* exist
        if not database.check_user_exists(user):
            error_message += f"User {str(user)} does not exist\n"
    
    if error_message:
        exit(error_message)

if __name__ == "__main__":
    main()

# Fail the pipeline if/when something goes wrong
sys.exit(1)