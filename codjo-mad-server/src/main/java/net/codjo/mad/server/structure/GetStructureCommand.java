package net.codjo.mad.server.structure;
import net.codjo.mad.server.handler.HandlerCommand;
import net.codjo.mad.server.handler.HandlerException;
/**
 *
 */
public class GetStructureCommand extends HandlerCommand {
    private StructureHome structureHome;


    public GetStructureCommand(StructureHome structureHome) {
        this.structureHome = structureHome;
    }


    @Override
    public CommandResult executeQuery(CommandQuery query) throws HandlerException {
        try {
            return createResult(structureHome.getStructure());
        }
        catch (Exception e) {
            throw new HandlerException("Impossible d'executer le handler GetStructure", e);
        }
    }
}
