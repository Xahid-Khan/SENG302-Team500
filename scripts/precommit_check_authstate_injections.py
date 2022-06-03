"""
Pre-Commit script which checks that there are no occurrences of illegal
AuthState usage in the portfolio codebase.
"""
import os
import re

PORTFOLIO_DIR = os.path.realpath(os.path.join(os.path.dirname(os.path.realpath(__file__)), "..", "portfolio", "src", "main", "java"))

RAISE_ERRORS = "PRECOMMIT_CHECK_AUTHSTATE_INJECTIONS_RAISE_ERRORS" in os.environ

ILLEGAL_REGEX = re.compile(r"@AuthenticationPrincipal\s+AuthState\s+", re.MULTILINE)


def walk_java_files(dir: str):
  for dirpath, dirnames, filenames in os.walk(dir):
    for filename in filenames:
      if filename.endswith(".java"):
        yield os.path.join(dirpath, filename)


def validate_file(filepath: str):
  with open(filepath, 'r') as f:
    content = f.read()
    if ILLEGAL_REGEX.search(content):
      return False
  return True


print("Running AuthState verifier pre-commit hook... If it emits false errors, you can retry with git commit --no-verify, or (better) fix the script in scripts/precommit_check_authstate_injections.py")

has_error = False
for filepath in walk_java_files(PORTFOLIO_DIR):
  try:
    valid = validate_file(filepath)
    if not valid:
      print(f"{filepath} contains potentially incorrect use of AuthState.")
      has_error = True
  except Exception as ex:
    print(f"Error while checking file: {filepath}. Skipping for now...")

    if RAISE_ERRORS:
      raise ex

if has_error:
  print("Cancelling commit until the above errors are resolved. Try again with git commit --no-verify to override.")
  exit(1)
else:
  print("No errors detected.")
  exit(0)