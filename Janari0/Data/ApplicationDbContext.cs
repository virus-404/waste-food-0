using System;
using System.Collections.Generic;
using System.Text;
using Janari0.Models;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using MySql.EntityFrameworkCore.Extensions;


namespace Janari0.Data
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
            : base(options)
        {
        }
        public DbSet<UserApp> Users { get; set;}
        public DbSet<Product> Products { get; set; }
        public DbSet<Stock> Stocks { get; set; }



        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Product>().ToTable("Products");
            modelBuilder.Entity<Stock>().HasKey(x => new { x.Id, x.IDProduct });
        }
    }
}


/*
 To know how works the migrations(create DB in MySQL):

https://docs.microsoft.com/en-us/ef/core/managing-schemas/migrations/?tabs=dotnet-core-cli



To do secrets(no show connection string -> only windows):
https://code-maze.com/aspnet-configuration-securing-sensitive-data/
 */