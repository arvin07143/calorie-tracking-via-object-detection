import argparse
import json

import numpy as np
import requests
from PIL import Image
from keras.preprocessing import image
import tensorflow as tf

ap = argparse.ArgumentParser()
ap.add_argument("-i", "--image", required=True)
args = vars(ap.parse_args())

image_path = args['image']

API_PATH = "http://localhost:8501/v1/models/my_model:predict"


# Preprocessing

def load_image_into_numpy_array(path):
    """Load an image from file into a numpy array.

    Puts image into numpy array to feed into tensorflow graph.
    Note that by convention we put it into a numpy array with shape
    (height, width, channels), where channels=3 for RGB.

    Args:
      path: the file path to the image

    Returns:
      uint8 numpy array with shape (img_height, img_width, 3)
    """
    return np.array(Image.open(path).convert('RGB'))


image_np = load_image_into_numpy_array(image_path)

# this line is added because of a bug in tf_serving(1.10.0-dev)


payload = {
    "instances": [image_np.tolist()]
}

r = requests.post(API_PATH, json=payload)
pred = (r.json())["predictions"][0]

print(json.dumps(['detection_scores']))

# Since only the first n items above threshold is valid and the list is sorted by score we can store the first n
# items in a list
detected_items_index = [i for i in range(len(pred['detection_scores'])) if pred['detection_scores'][i] > 0.75]
for i in detected_items_index:
    print(pred['detection_boxes'][i])
    print(pred['detection_classes'][i])
    print(pred['detection_scores'][i])


