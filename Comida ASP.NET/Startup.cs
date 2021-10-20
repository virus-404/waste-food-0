using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(Comida_ASP.NET.Startup))]
namespace Comida_ASP.NET
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
