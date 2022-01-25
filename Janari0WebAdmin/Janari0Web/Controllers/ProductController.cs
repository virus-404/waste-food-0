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
    public class ProductController : Controller
    {
        DBOperations db = new DBOperations();
        public async Task<ActionResult> IndexAsync()
        {
            List<Product> products = await db.GetAllProducts();
            return View(products);
        }

        // GET: User/Details/5
        public async Task<ActionResult> DetailsAsync(string id)
        {
            Product product = await db.GetProductData(id);
            return View(product);
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
            Product product = await db.GetProductData(id);
            return View(product);
        }

        // POST: User/Edit/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Edit(Product product)
        {
            db.UpdateProduct(product);
            return RedirectToAction(nameof(Index));
        }

        // GET: User/Delete/5
        public async Task<ActionResult> DeleteAsync(string id)
        {
            Product product = await db.GetProductData(id);
            return View(product);
        }

        // POST: User/Delete/5
        [HttpPost]
        [ValidateAntiForgeryToken]
        public ActionResult Delete(string id)
        {
            db.DeleteProduct(id);
            return RedirectToAction(nameof(Index));
        }
    }
}