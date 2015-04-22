package pl.edu.agh.automerger.bean;

import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import pl.edu.agh.automerger.mail.MailSender;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by wawek on 18.04.15.
 */
@Stateless
public class AutomergerBean {

    private Logger logger = Logger.getLogger("AutomergerBean");

    @EJB
    private ConfigurationBean configurationBean;

    @EJB
    private MailSender mailSender;

    private Git git;
    private String localPath, remotePath;
    private Repository localRepo;


    public AutomergerBean() {

    }

    @PostConstruct
    public void init() throws IOException {
        logger.info("AutomergerBean.init - invoked");
        localPath = configurationBean.getProperty("automerger.localpath");
        remotePath = configurationBean.getProperty("automerger.remotepath");
        localRepo = new FileRepository(localPath + "/.git");
        git = new Git(localRepo);
    }

    @Schedule(persistent = false, hour = "*", minute = "0", second = "0")
    public void merge() {
        logger.info("AutomergerBean.merge - invoked");
        try {
            CheckoutCommand checkoutCommand = git.checkout();
            checkoutCommand.setName(configurationBean.getProperty("automerger.master"));
            checkoutCommand.setCreateBranch(false); // probably not needed, just to make sure
            checkoutCommand.call(); // switch to "master" branch

            MergeCommand mergeCommand = git.merge();
            Ref branch = localRepo.getRef(configurationBean.getProperty("automerger.external_branch"));
            mergeCommand.include(branch);
            MergeResult res = mergeCommand.call(); // actually do the merge

            if (res.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
                logger.info("AutomergerBean.merge - conflicts exist");
                // call mail sender
                // admin for this moment
                mailSender.send(configurationBean.getProperty("automerger.mail"));
            } else {
                logger.info("AutomergerBean.merge - no conflicts");
                PushCommand pushCommand = git.push();
                pushCommand.call();
            }
        } catch (GitAPIException e) {
            logger.warning("AutomergerBean.merge - GitAPIException " + e);
        } catch (IOException e) {
            logger.warning("AutomergerBean.merge - IOException " + e);
        }
    }
}
