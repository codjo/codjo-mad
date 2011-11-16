package net.codjo.mad.server.plugin;
import net.codjo.mad.server.handler.AspectBranchLauncher;
import net.codjo.mad.server.handler.AspectBranchLauncherFactory;
/**
 *
 */
class FailingAspectLauncherFactory implements AspectBranchLauncherFactory {
    public AspectBranchLauncher create() {
        throw new UnsupportedOperationException(
              "Aucun moteur permettant de gérer les aspects en mode fork n'a été configuré."
              + " Avez-vous ajouté le plugin WorkflowServerPlugin après MadServerPlugin ?");
    }
}
