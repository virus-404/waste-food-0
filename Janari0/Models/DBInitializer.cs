using System;
using System.Linq;
using Janari0.Data;

namespace Janari0.Models
{
    public class DBInitializer
    {
        public static void InitializeAsync(ApplicationDbContext context)
        {
            context.Database.EnsureCreated();

            /*if (context.Users.Any())
            {
                return;
            }*/
        }
    }
}
