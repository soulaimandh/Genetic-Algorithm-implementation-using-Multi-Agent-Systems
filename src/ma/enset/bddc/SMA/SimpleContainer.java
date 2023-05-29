package ma.enset.bddc.SMA;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import ma.enset.bddc.GAUtils;

public class SimpleContainer {
    public static void main(String[] args) throws Exception {
        Runtime runtime=Runtime.instance();
        ProfileImpl profile=new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST,"localhost");
        AgentContainer agentContainer = runtime.createAgentContainer(profile);

        AgentController masterAgent = agentContainer.createNewAgent("masterAgent",MasterAgent.class.getName(),new Object[]{});
        masterAgent.start();

        for (int i=0; i< GAUtils.ISLAND_NUMBEER; i++){
            AgentController islandAgent = agentContainer.createNewAgent("IslandAgent"+i,IslandAgent.class.getName(),new Object[]{});
            islandAgent.start();
        }

    }
}
