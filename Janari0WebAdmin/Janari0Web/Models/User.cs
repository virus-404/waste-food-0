using System;
using System.ComponentModel.DataAnnotations;
using Google.Cloud.Firestore;
using Janari0Web.Controllers;

namespace Janari0Web.Models
{
    public class User
    {
        [Required]
        [EmailAddress]
        public string Email { get; set; }
        [Required]
        public string Password { get; set; }
    }

    [FirestoreData]
    public class UserFire
    {
        public string UserID { get; set; }
        [FirestoreProperty]
        [Display(Name = "Username")]
        public string username { get; set; }
        [FirestoreProperty]
        [Display(Name = "Phone number")]
        public string phoneNumber { get; set; }
        [FirestoreProperty]
        [Display(Name = "Longitude")]
        public float lon { get; set; }
        [FirestoreProperty]
        [Display(Name = "Latitude")]
        public float lat { get; set; }
        [FirestoreProperty]
        [Display(Name = "Role")]
        public string role { get; set; }

    }
}