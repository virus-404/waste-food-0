using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Linq;
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


        private Product DictToProduct(Dictionary<string, object> dict)
        {
            var expd = dict["expirationDate"].ToString();
            var result = expd.Split("p: ");
            var timestr = result[1].Replace("T", " ");
            timestr = timestr.Replace("Z", "");
            DateTime date = DateTime.ParseExact(timestr, "yyyy-MM-dd HH:mm:ss", System.Globalization.CultureInfo.InvariantCulture);
            var name = dict["name"].ToString();
            var id = dict["id"].ToString();
            var phot = (List<object>)dict["photos"];
            List<string> photos = new List<string>();
            foreach (var item in phot)
            {
                photos.Add(item.ToString());
            }
            Product newProd = new Product()
            {
                id = id,
                name = name,
                photos = photos,
                expirationDate = date
            };
            return newProd;
        }
        public async Task<List<Product>> GetAllProducts()
        {
            Query collection = db.Collection("products");
            QuerySnapshot collsp = await collection.GetSnapshotAsync();
            List<Product> products = new List<Product>();
            foreach (DocumentSnapshot docSnap in collsp.Documents)
            {
                if (docSnap.Exists)
                {
                    Dictionary<string, object> dict = docSnap.ToDictionary();
                    Product newProd = DictToProduct(dict);
                    products.Add(newProd);
                }
            }
            return products;
        }
        public async Task<Product> GetProductData(string id)
        {
            DocumentSnapshot snapshot = await db.Collection("products").Document(id).GetSnapshotAsync();
            if (snapshot.Exists)
            {
                Dictionary<string, object> dict = snapshot.ToDictionary();
                Product newProd = DictToProduct(dict);
                return newProd;
            }
            else
            {
                return null;
            }
        }
        public async void UpdateProduct(Product p)
        {
            DocumentReference userRef = db.Collection("products").Document(p.id);
            Product prr = await GetProductData(p.id);
            ProductFire pf = new ProductFire()
            {
                id = p.id,
                name = p.name,
                expirationDate = Timestamp.FromDateTime(p.expirationDate.ToUniversalTime()),
                photos = prr.photos
            };
            await userRef.SetAsync(pf, SetOptions.Overwrite);
        }
        public async void DeleteProduct(string id)
        {
            DocumentReference userRef = db.Collection("products").Document(id);
            await userRef.DeleteAsync();
        }
    }
}
