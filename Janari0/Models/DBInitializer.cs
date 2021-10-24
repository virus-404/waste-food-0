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
            createProduct(context);
            createInitialState(context, userManager);

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


        private static void createProduct(ApplicationDbContext context)
        {
            if (context.Products.Any())
            {
                return;
            }

            Product p1 = new Product
            {
                name = "Fuet",
                brand = "Casa Tarradelles",
            };
            Product p2 = new Product
            {
                name = "Pa",
                brand = "Bimbo",
            };
            Product p3 = new Product
            {
                name = "Llet",
                brand = "Llet nostra",
            };
            Product p4 = new Product
            {
                name = "Galetes",
                brand = "Oreo",
            };
            context.AddRange(p1, p2, p3, p4);
            context.SaveChanges();

        }

        private static void createInitialState(ApplicationDbContext context, UserManager<UserApp> userManager)
        {
            var defaultUser = new UserApp
            {
                UserName = "user@user.com",
                Email = "user@user.com",
                firstName = "user",
                lastName = "user2",
                EmailConfirmed = true,
                PhoneNumberConfirmed = true
            };
            var userexist = userManager.Users.Where(x => x.Email == defaultUser.Email).FirstOrDefault();
            var user = userManager.FindByEmailAsync(defaultUser.Email);
            if (userexist == null)
            {
                userManager.CreateAsync(defaultUser, "A@a1234567");
                userManager.AddToRoleAsync(defaultUser, Roles.User.ToString());
                Product p1 = context.Products.Where(x => x.name == "Llet").FirstOrDefault();
                Product p2 = context.Products.Where(x => x.name == "Galetes").FirstOrDefault();

                Stock s1 = new Stock {
                    Id = defaultUser.Id,
                    IDProduct = p1.IDProduct,
                    expirationDate = new DateTime(2022, 12, 01),
                    stock = 2,
                };

                Stock s2 = new Stock
                {
                    Id = defaultUser.Id,
                    IDProduct = p2.IDProduct,
                    expirationDate = new DateTime(2021, 10, 11),
                    stock = 5,
                };
                context.AddRange(s1, s2);
                context.SaveChanges();
            }

        }


    }
}
