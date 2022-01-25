using System;
using System.Collections.Generic;
using System.IO;
using System.Threading.Tasks;
using Google.Cloud.Firestore;
using Janari0Web.Models;
using Newtonsoft.Json;
using RestSharp;

namespace Janari0Web.Controllers
{
    public class DBOperations
    {
        FirestoreDb db;
        public DBOperations()
        {
            string patrh = Directory.GetCurrentDirectory();
            string fileName = "/credential.json";
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", patrh + fileName);
            db = FirestoreDb.Create("janari0-b4e82");
        }

        public async Task<List<UserFire>> GetAllUsers()
        {
            Query collection = db.Collection("users");
            QuerySnapshot collsp = await collection.GetSnapshotAsync();
            List<UserFire> users = new List<UserFire>();
            foreach (DocumentSnapshot docSnap in collsp.Documents)
            {
                if (docSnap.Exists)
                {
                    Dictionary<string, object> dict = docSnap.ToDictionary();
                    string json = JsonConvert.SerializeObject(dict);
                    UserFire newUser = JsonConvert.DeserializeObject<UserFire>(json);
                    newUser.UserID = docSnap.Id;
                    users.Add(newUser);
                }
            }
            return users;
        }
        public async Task<UserFire> GetUserData(string id)
        {
            DocumentSnapshot snapshot = await db.Collection("users").Document(id).GetSnapshotAsync();
            if (snapshot.Exists)
            {
                Dictionary<string, object> dict = snapshot.ToDictionary();
                string json = JsonConvert.SerializeObject(dict);
                UserFire newUser = JsonConvert.DeserializeObject<UserFire>(json);
                newUser.UserID = snapshot.Id;
                return newUser;
            }
            else
            {
                return null;
            }
        }
        public async Task<Boolean> UserIsAdmin(string id)
        {
            UserFire user = await GetUserData(id);
            if(user != null)
            {
                if (user.role.Equals("admin"))
                {
                    return true;
                }
            }
            return false;
        }
        public async void UpdateUser(UserFire user)
        {
            DocumentReference userRef = db.Collection("users").Document(user.UserID);
            
            await userRef.SetAsync(user, SetOptions.Overwrite);
        }
        public async void DeleteUser(string id)
        {
            DocumentReference userRef = db.Collection("users").Document(id);
            await userRef.DeleteAsync();
        }
    }
}
