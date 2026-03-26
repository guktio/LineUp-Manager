import subprocess
import sys
import argparse
from dotenv import load_dotenv
import os

_ = load_dotenv()

USER = os.getenv("USER")
IMAGE_NAME_DEFAULT = os.getenv("IMAGE_NAME")
HOST = os.getenv("HOST")
PATH = os.getenv("PATH")
parser = argparse.ArgumentParser(description="Deploy Docker image to server")
_ = parser.add_argument("--name", default=IMAGE_NAME_DEFAULT, help="Name of docker iamge")
_ = parser.add_argument("--tag", default="latest", help="Tag in docker image")
_ = parser.add_argument("--user", default=USER, help="SSH user")
_ = parser.add_argument("--host", default=HOST, help="SSH host")
_ = parser.add_argument("--path", default=PATH, help="Remote path on the server")


args = parser.parse_args()

IMAGE_NAME = args.name  # pyright: ignore[reportAny]
IMAGE_TAG = args.tag # pyright: ignore[reportAny]

REMOTE_USER = args.user # pyright: ignore[reportAny]
REMOTE_HOST = args.host # pyright: ignore[reportAny]
REMOTE_PATH = "~/"

TAR_FILE = f"{IMAGE_NAME}-{IMAGE_TAG}.tar"
SSH_PATH = "C:/Windows/System32/OpenSSH/ssh.exe"
SCP_PATH = "C:/Windows/System32/OpenSSH/scp.exe"

def run_command(command:list[str]):
    """Run a shell command and exit on failure."""
    try:
        print(f"Running: {' '.join(command)}")
        _ = subprocess.run(command, check=True)
    except subprocess.CalledProcessError as e:
        print(f"Error running command: {e}")
        sys.exit(1)

def connect_ssh(user: str, host: str):
    """Run a ssh connection to server and exit on failure."""
    try:
        _ = subprocess.run([SSH_PATH, f"{user}@{host}"])
    except subprocess.CalledProcessError as e:
        print(f"Error running command: {e}")
        sys.exit(1)

def main():
    if IMAGE_NAME and IMAGE_TAG and TAR_FILE and REMOTE_USER and REMOTE_HOST and REMOTE_PATH:
        run_command(["docker", "build", "-t", f"{IMAGE_NAME}:{IMAGE_TAG}", "."])

        run_command(["docker", "save", "-o", TAR_FILE, f"{IMAGE_NAME}:{IMAGE_TAG}"])

        print(f"Docker image saved to {TAR_FILE}")

        remote_dest = f"{REMOTE_USER}@{REMOTE_HOST}:{REMOTE_PATH}"
        run_command([SCP_PATH, TAR_FILE, remote_dest])

        print(f"File {TAR_FILE} successfully copied to {remote_dest}")
        print(f"Ssh connect to server User={REMOTE_USER} Server={REMOTE_HOST}")
        connect_ssh(REMOTE_USER,REMOTE_HOST)  # pyright: ignore[reportAny]
    else:
        print("Failed")
if __name__ == "__main__":
    main()