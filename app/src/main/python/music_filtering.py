import pandas as pd
from sklearn.cluster import KMeans
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split
from lightgbm import LGBMClassifier

# Load data from CSV file
data = pd.read_csv(r'c:\Users\Triwira Bawangun\OneDrive\Dokumen\Kuliah\Tugas Akhir\dataset\data_musik.csv')

data.head()

# Drop duplicates based on 'name' column
data.drop_duplicates(inplace=True, subset=['name'])

# Store the 'name' column in a separate variable
name = data['name']

# Select the features for content-based filtering
col_features = ['acousticness', 'danceability', 'energy', 'valence']

# Normalize the features
scaler = MinMaxScaler()
X = scaler.fit_transform(data[col_features])

# Apply K-means clustering for collaborative filtering
kmeans = KMeans(n_clusters=4, random_state=15).fit(X)

# Add 'kmeans' labels and 'song_name' back to the data
data['kmeans'] = kmeans.labels_
data['song_name'] = name

# Create a copy of the original data
og_data = data.copy()

# Group the data by 'kmeans' labels
cluster = data.groupby(by=data['kmeans'])

# Prepare the input features for collaborative filtering
y = data.pop('kmeans')
x = data.drop(columns=['name', 'artists', 'id', 'release_date', 'song_name'])

# Split the data into training and testing sets for collaborative filtering
x_train, x_test, y_train, y_test = train_test_split(x, y, test_size=0.25)

# Train the LGBMClassifier model for collaborative filtering
model_collab = LGBMClassifier().fit(x_train, y_train)

# Evaluate the model's performance for collaborative filtering
train_score_collab = model_collab.score(x_train, y_train)
test_score_collab = model_collab.score(x_test, y_test)

# Group the data by 'kmeans' labels and sort by 'popularity' for content-based filtering
df = cluster.apply(lambda x: x.sort_values(["popularity"], ascending=False))
df.reset_index(level=0, inplace=True)

# Define the emotions
EMOTIONS = ["happy", "sad", "angry", "neutral"]

# Function to get recommended songs based on emotion using hybrid filtering
def get_results(emotion_code, NUM_RECOMMEND=10):
    emotion_set = []
    if emotion_code in range(len(EMOTIONS)):
        emotion_set.append(df[df['kmeans'] == emotion_code]['song_name'].head(NUM_RECOMMEND))
        return pd.DataFrame(emotion_set).T

# User input for emotion and number of recommendations
emotion_word = input("Enter your emotion (happy/sad/angry/neutral): ").lower()
NUM_RECOMMEND = int(input("Enter number of recommendations: "))

# Determine the emotion code based on user input
if emotion_word == 'happy':
    emotion_code = 0
elif emotion_word == 'sad':
    emotion_code = 1
elif emotion_word == 'angry':
    emotion_code = 2
else:
    emotion_code = 3

# Get the recommended songs based on the emotion using hybrid filtering
results = get_results(emotion_code, NUM_RECOMMEND)
print(results)

# User input for emotion and number of recommendations
emotion_word = input("Enter your emotion (happy/sad/angry/neutral): ").lower()
NUM_RECOMMEND = int(input("Enter number of recommendations: "))

# Determine the emotion code based on user input
if emotion_word == 'happy':
    emotion_code = 0
elif emotion_word == 'sad':
    emotion_code = 1
elif emotion_word == 'angry':
    emotion_code = 2
else:
    emotion_code = 3

# Get the recommended songs based on the emotion using hybrid filtering
results = get_results(emotion_code, NUM_RECOMMEND)
print(results)

# User input for emotion and number of recommendations
emotion_word = input("Enter your emotion (happy/sad/angry/neutral): ").lower()
NUM_RECOMMEND = int(input("Enter number of recommendations: "))

# Determine the emotion code based on user input
if emotion_word == 'happy':
    emotion_code = 0
elif emotion_word == 'sad':
    emotion_code = 1
elif emotion_word == 'angry':
    emotion_code = 2
else:
    emotion_code = 3

# Get the recommended songs based on the emotion using hybrid filtering
results = get_results(emotion_code, NUM_RECOMMEND)
print(results)

# User input for emotion and number of recommendations
emotion_word = input("Enter your emotion (happy/sad/angry/neutral): ").lower()
NUM_RECOMMEND = int(input("Enter number of recommendations: "))

# Determine the emotion code based on user input
if emotion_word == 'happy':
    emotion_code = 0
elif emotion_word == 'sad':
    emotion_code = 1
elif emotion_word == 'angry':
    emotion_code = 2
else:
    emotion_code = 3

# Get the recommended songs based on the emotion using hybrid filtering
results = get_results(emotion_code, NUM_RECOMMEND)
print(results)



