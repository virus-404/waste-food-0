using System;
using System.ComponentModel.DataAnnotations;

namespace Janari0.Models
{
    public class Product
    {
        [Key]
        public int IDProduct { get; set; }
        public string name { get; set; }
        public string barCode { get; set; }
        public string brand { get; set; }
        public float popularity { get; set; }

    }
}
