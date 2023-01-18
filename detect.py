import torch
paths = ['detect_img/IMG_20230117_131420.jpg',
         'detect_img//IMG_20230117_131335.jpg',
         'detect_img//IMG_20230117_131507.jpg']
model = torch.hub.load('ultralytics/yolov5', 'custom',
                       path='model/best.pt')
model.conf = 0.5
results = model(paths)
for elem in results.pandas().xyxy:
    print(elem)

