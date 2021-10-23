using System;
using System.Linq;
using Janari0.Data;
using Microsoft.AspNetCore.Identity;

namespace Janari0.Models
{
    public class DBInitializer
    {
        public static void InitializeAsync(ApplicationDbContext context, UserManager<UserApp> userManager, RoleManager<IdentityRole> roleManager)
        {

            context.Database.EnsureCreated();

            createRoles(roleManager);
            createDefaultUser(userManager);

        }
        public enum Roles { Admin, User, Company };
        private static void createRoles(RoleManager<IdentityRole> roleManager)
        {
            if (roleManager.Roles.Any())
            {
                return;
            }
            roleManager.CreateAsync(new IdentityRole(Roles.Admin.ToString()));
            roleManager.CreateAsync(new IdentityRole(Roles.User.ToString()));
            roleManager.CreateAsync(new IdentityRole(Roles.Company.ToString()));
        }

        private static void createDefaultUser(UserManager<UserApp> userManager)
        {
            var defaultUser = new UserApp
            {
                UserName = "admin@admin.com",
                Email = "admin@admin.com",
                firstName = "admin",
                lastName = "admin2",
                EmailConfirmed = true,
                PhoneNumberConfirmed = true
            };
            var userexist = userManager.Users.Where(x => x.Email == defaultUser.Email).FirstOrDefault();
            var user = userManager.FindByEmailAsync(defaultUser.Email);
            if (userexist == null)
            {
                userManager.CreateAsync(defaultUser, "A@a1234567");
                userManager.AddToRoleAsync(defaultUser, Roles.Admin.ToString());
            }

            
        }

        
    }
}
