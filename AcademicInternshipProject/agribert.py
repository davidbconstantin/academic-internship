from transformers import pipeline

file = open("text.txt")
data = file.read()
print(data)
sentiment_pipeline = pipeline("sentiment-analysis")
result = sentiment_pipeline(data)
print(result)
print("Press any key to continue...")
message = input()