using System;
using Janari0Web.Controllers;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;

namespace Janari0Web.Filters
{
    public class SampleActionFilter : IActionFilter
    {
        
        public void OnActionExecuting(ActionExecutingContext context)
        {
            var token = context.HttpContext.Session.GetString("_UserToken");
            string path = context.HttpContext.Request.Path.ToString();
            if (token == null && path != "/Home/SignIn")
            {
                context.Result = new RedirectResult("Home/SignIn");
                //var controller = (HomeController)context.Controller;
                //context.Result = controller.RedirectToAction("SignIn", "Home");
            }
        }

        public void OnActionExecuted(ActionExecutedContext context)
        {
            // Do something after the action executes.
        }
    }
}
