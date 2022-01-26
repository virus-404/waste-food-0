using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Janari0Web.Models;
using Firebase.Auth;
using Microsoft.AspNetCore.Http;

namespace Janari0Web.Controllers
{
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        FirebaseAuthProvider auth;
        DBOperations db = new DBOperations();
        public HomeController(ILogger<HomeController> logger)
        {
            _logger = logger;
            auth = new FirebaseAuthProvider(
                               new FirebaseConfig("AIzaSyD91o8tNVKoS_K0dM66davEVfuqCsz7eGE"));
            
        }

        public IActionResult SignIn()
        {
            return View();
        }
        [HttpPost]
        public async Task<IActionResult> SignIn(Models.User userModel)
        {
            //log in the user
            string token = null;
            ViewBag.ErrorLogin = "";
            Firebase.Auth.User user = null;
            try
            {
                var fbAuthLink = await auth
                            .SignInWithEmailAndPasswordAsync(userModel.Email, userModel.Password);
                user = fbAuthLink.User;
                var admin = await db.UserIsAdmin(user.LocalId);
                if (admin)
                {
                    token = fbAuthLink.FirebaseToken;
                }
                else
                {
                    ViewBag.ErrorLogin = "No admin role";
                }

            }
            catch
            {
                ViewBag.ErrorLogin = "Email or password incorrect";
                return View(userModel);
            }
            //saving the token in a session variable
            if (token != null)
            {
                HttpContext.Session.SetString("_UserToken", token);
                HttpContext.Session.SetString("_UserName", user.DisplayName);
                return RedirectToAction("Index");
            }
            else
            {
                return View();
            }
        }

        public IActionResult LogOut()
        {
            HttpContext.Session.Remove("_UserToken");
            return RedirectToAction("SignIn");
        }

        public IActionResult Index()
        {
            var token = HttpContext.Session.GetString("_UserToken");
            if (token != null)
            {
                return View();
            }
            else
            {
                return RedirectToAction("SignIn");
            }
        }


        public IActionResult Privacy()
        {
            return View();
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
    }
}
