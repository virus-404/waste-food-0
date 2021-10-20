using System;
using Microsoft.AspNetCore.Identity;

namespace Janari0.Models
{
    public class UserApp : IdentityUser
    {
        public string firstName { get; set; }
        public string lastName { get; set; }
    
    }
}
