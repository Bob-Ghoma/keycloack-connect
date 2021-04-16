public class main {
    public static void main(String[] args) {
       // Auth auth = new Auth();
      //  auth.openAuthorizationPage();
      //  auth.runHttpServer();
        AuthGitLab authGitLab = new AuthGitLab();
        authGitLab.openAuthorizationPage();
        authGitLab.runHttpServer();
    }
}
