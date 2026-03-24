import subprocess
import sys

IMAGE_NAME = "lineup"
IMAGE_TAG = "0.2.1"
TAR_FILE = f"{IMAGE_NAME}-{IMAGE_TAG}.tar"

REMOTE_USER = "guktio"
REMOTE_HOST = "192.168.9.50"
PORT = 22
REMOTE_PATH = "~/"

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
        _ = subprocess.run(["C:/Windows/System32/OpenSSH/ssh.exe", f"{user}@{host}"])
    except subprocess.CalledProcessError as e:
        print(f"Error running command: {e}")
        sys.exit(1)

def main():
    if IMAGE_NAME and IMAGE_TAG and TAR_FILE and REMOTE_USER and REMOTE_HOST and REMOTE_PATH:
        run_command(["docker", "build", "-t", f"{IMAGE_NAME}:{IMAGE_TAG}", "."])

        run_command(["docker", "save", "-o", TAR_FILE, f"{IMAGE_NAME}:{IMAGE_TAG}"])

        print(f"Docker image saved to {TAR_FILE}")

        remote_dest = f"{REMOTE_USER}@{REMOTE_HOST}:{REMOTE_PATH}"
        run_command(["C:/Windows/System32/OpenSSH/scp.exe", TAR_FILE, remote_dest])

        print(f"File {TAR_FILE} successfully copied to {remote_dest}")
        print(f"Ssh connect to server User={REMOTE_USER} Server={REMOTE_HOST}")
        connect_ssh(REMOTE_USER,REMOTE_HOST)
    else:
        print("Gamno")
if __name__ == "__main__":
    main()