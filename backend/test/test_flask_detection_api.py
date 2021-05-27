# importing the requests library
import argparse
import base64
import json

import requests

# defining the api-endpoint
API_ENDPOINT = "http://localhost:5000/detection/predict/"

# taking input image via command line
ap = argparse.ArgumentParser()
ap.add_argument("-i", "--image", required=True,
                help="path of the image")
args = vars(ap.parse_args())

image_path = args['image']

# Encoding the JPG,PNG,etc. image to base64 format
with open(image_path, "rb") as imageFile:
    b64_image = base64.b64encode(imageFile.read())

# data to be sent to api
data = {'b64': b64_image}

# sending post request and saving response as response object
r = requests.get(url=API_ENDPOINT, params=data)
pred = r.json()
# extracting the response
print(json.dumps(pred, indent=2))
