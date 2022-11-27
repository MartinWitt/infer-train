package io.github.martinwitt.infer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return execCmd("infer help");
    }
    
    private static String execCmd(String cmd) {
        String result = null;
        try (InputStream inputStream = Runtime.getRuntime().exec(cmd).getInputStream();
                Scanner s = new Scanner(inputStream).useDelimiter("\\A")) {
            result = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public String executeInfer(String repoLink)
            throws InvalidRemoteException, TransportException, GitAPIException {
        try (Git git = Git.cloneRepository().setNoCheckout(true).setURI(repoLink).call()) {
            File workDir = git.getRepository().getWorkTree();
        } catch (Exception e) {
            // TODO: Delete folder if exists
            e.printStackTrace();
        }

        return "Hello from RESTEasy Reactive";
    }
}