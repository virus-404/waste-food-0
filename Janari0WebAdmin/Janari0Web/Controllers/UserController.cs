using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Firebase.Database;
using Firebase.Database.Query;
using Google.Cloud.Firestore;
using System.IO;
using Newtonsoft.Json;
using Janari0Web.Controllers;

namespace Janari0Web.Models
{
    public class UserController : Controller
    {
        DBOperations db = new DBOperations();
        public async Task<ActionResult> IndexAsync()
        {
            List<UserFire> users = await db.GetAllUsers();
            return View(users);
        }

        // GET: User/Details/5
        public async Task<ActionResult> DetailsAsync(string id)
        {
            UserDetails ud = new UserDetails();
            ud.user = await db.GetUserData(id);
            ud.products= await db.GetAllProductsUser(id);
            ud.productsSD = await db.GetAllProductsSDUser(id);
            return View(ud);
        }
         
        // GET: User/Create
        public ActionResult Create()
        {
            return View();
        }

        // POST: User/Create
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Create(IFormCollection collection)
        {
            try
            {
                // TODO: Add insert logic here

                return RedirectToAction(nameof(Index));
            }
            catch
            {
                return View();
            }
        }

        // GET: User/Edit/5
        public async Task<ActionResult> EditAsync(string id)
        {
            UserFire user = await db.GetUserData(id);
            return View(user);
        }

        // POST: User/Edit/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit(UserFire user)
        {
            db.UpdateUser(user);
            return RedirectToAction(nameof(Index));
        }

        // GET: User/Delete/5
        public async Task<ActionResult> DeleteAsync(string id)
        {
            UserFire user = await db.GetUserData(id);
            return View(user);
        }

        // POST: User/Delete/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Delete(string id)
        {
            db.DeleteUser(id);
            return RedirectToAction(nameof(Index));
        }
    }
}