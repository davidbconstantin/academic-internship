from transformers import pipeline

file = open("text.txt")
print(file.read())
fill_mask = pipeline(
    "fill-mask",
    model="recobo/agriculture-bert-uncased",
    tokenizer="recobo/agriculture-bert-uncased"
)
data = fill_mask("[MASK] is the [MASK] of cultivating plants and [MASK].")
print(data)
print("Press any key to continue...")
message = input()