using System;
using System.ComponentModel.DataAnnotations.Schema;

namespace Janari0.Models
{
    public class Stock
    {
        [ForeignKey("UserApp")]
        public string Id { get; set; }
        public UserApp UserApp { get; set; }

        [ForeignKey("Product")]
        public int IDProduct { get; set; }
        public Product Product { get; set; }

        public int stock { get; set; }
        public DateTime expirationDate { get; set; }
    }
}
