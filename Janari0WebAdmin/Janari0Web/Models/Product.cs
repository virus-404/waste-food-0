using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using Google.Cloud.Firestore;
using Janari0Web.Controllers;

namespace Janari0Web.Models
{
    [FirestoreData]
    public class Product
    {
        [FirestoreProperty]
        [Display(Name = "ProductID")]
        public string id { get; set; }

        [FirestoreProperty]
        [Display(Name = "Name")]
        public string name { get; set; }

        [FirestoreProperty]
        [DisplayFormat(DataFormatString = "{0:dd/MM/yyyy}")]
        [Display(Name = "Expiration date")]
        public DateTime expirationDate { get; set; }

        [FirestoreProperty]
        [Display(Name = "Photos")]
        public List<string> photos { get; set; }

    }
    [FirestoreData]
    public class ProductFire
    {
        [FirestoreProperty]
        [Display(Name = "ProductID")]
        public string id { get; set; }

        [FirestoreProperty]
        [Display(Name = "Name")]
        public string name { get; set; }

        [FirestoreProperty]
        [Display(Name = "Expiration date")]
        public Timestamp expirationDate { get; set; }

        [FirestoreProperty]
        [Display(Name = "Photos")]
        public List<string> photos { get; set; }
    }
    [FirestoreData]
    public class ProductSD
    {
        [FirestoreProperty]
        [Display(Name = "ProductSDID")]
        public string id { get; set; }

        [FirestoreProperty]
        [Display(Name = "Description")]
        public string description { get; set; }

        [FirestoreProperty]
        [Display(Name = "Longitude")]
        public float lon { get; set; }
        [FirestoreProperty]
        [Display(Name = "Latitude")]
        public float lat { get; set; }

        [FirestoreProperty]
        [Display(Name = "Phone number")]
        public string phoneNumber { get; set; }

        [FirestoreProperty]
        [Display(Name = "Price")]
        public string price { get; set; }
        [FirestoreProperty]
        [Display(Name = "Product")]
        public Product product { get; set; }
    }
}