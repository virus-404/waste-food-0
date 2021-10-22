using System;
using System.Collections.Generic;
using System.Text;
using Janari0.Models;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using MySql.EntityFrameworkCore.Extensions;


namespace Janari0.Data
{

    public class ApplicationDbContext : IdentityDbContext<UserApp>
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
            : base(options)
        {
        }
        //public DbSet<UserApp> UsersApp { get; set; }
        public DbSet<Product> Products { get; set; }
        public DbSet<Stock> Stocks { get; set; }



        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            modelBuilder.Entity<IdentityUser>(entity => entity.Property(m => m.Id).HasMaxLength(80));
            modelBuilder.Entity<IdentityRole>(entity => entity.Property(m => m.Id).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserLogin<string>>(entity => entity.Property(m => m.LoginProvider).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserLogin<string>>(entity => entity.Property(m => m.ProviderKey).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserLogin<string>>(entity => entity.Property(m => m.UserId).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserRole<string>>(entity => entity.Property(m => m.UserId).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserRole<string>>(entity => entity.Property(m => m.RoleId).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserToken<string>>(entity => entity.Property(m => m.UserId).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserToken<string>>(entity => entity.Property(m => m.LoginProvider).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserToken<string>>(entity => entity.Property(m => m.Name).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserClaim<string>>(entity => entity.Property(m => m.Id).HasMaxLength(80));
            modelBuilder.Entity<IdentityUserClaim<string>>(entity => entity.Property(m => m.UserId).HasMaxLength(80));
            modelBuilder.Entity<IdentityRoleClaim<string>>(entity => entity.Property(m => m.Id).HasMaxLength(80));
            modelBuilder.Entity<IdentityRoleClaim<string>>(entity => entity.Property(m => m.RoleId).HasMaxLength(80));
            modelBuilder.Entity<Product>().ToTable("Products");
            modelBuilder.Entity<Stock>().ToTable("Stocks").HasKey(x => new { x.Id, x.IDProduct });
        }
    }

}


/*
 To know how works the migrations(create DB in MySQL):

https://docs.microsoft.com/en-us/ef/core/managing-schemas/migrations/?tabs=dotnet-core-cli



To do secrets(no show connection string -> only windows):
https://code-maze.com/aspnet-configuration-securing-sensitive-data/
 */