package ma.enset.bddc.SMA;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import ma.enset.bddc.GAUtils;
import ma.enset.bddc.Individual;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class IslandAgent extends Agent {
    private Individual[] population=new Individual[GAUtils.POPULATION_SIZE];
    private Individual individual1;
    public Individual individual2;

    @Override
    protected void setup() {
/*        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        dfAgentDescription.setName(getAID());
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName("island");
        serviceDescription.setType("island");
        dfAgentDescription.addServices(serviceDescription);

        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            throw new RuntimeException(e);
        }*/
        SequentialBehaviour sequentialBehaviour = new SequentialBehaviour();
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                initialize();
                sortPopulation();
            }
        });
        sequentialBehaviour.addSubBehaviour(new Behaviour() {
            int iter=0;
            @Override
            public void action() {
                crossover();
                mutation();
                sortPopulation();
                iter++;
            }

            @Override
            public boolean done() {
                return GAUtils.MAX_ITERATIONS==iter || getBestFintness()==GAUtils.CHROMOSOME_SIZE;
            }
        });
        sequentialBehaviour.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription dfAgentDescription = new DFAgentDescription();
                ServiceDescription serviceDescription = new ServiceDescription();
                serviceDescription.setType("ga");
                dfAgentDescription.addServices(serviceDescription);
                DFAgentDescription[] dfAgentDescriptions = null;
                try {
                    dfAgentDescriptions = DFService.search(getAgent(), dfAgentDescription);
                } catch (FIPAException e) {
                    e.printStackTrace();
                }
                ACLMessage aclMessage = new ACLMessage(ACLMessage.INFORM);

                aclMessage.addReceiver(dfAgentDescriptions[0].getName());

                aclMessage.setContent(Arrays.toString(population[0].getChromosome()) + " " + String.valueOf(population[0].getFitness()));
                send(aclMessage);
            }
        });
        addBehaviour(sequentialBehaviour);
    }

    public void initialize(){
        for (int i=0;i<GAUtils.POPULATION_SIZE;i++) {
            population[i]=new Individual();
            population[i].calculateFintess();
        }
    }

    public void crossover(){
        individual1=new Individual(population[0].getChromosome());
        individual2=new Individual(population[1].getChromosome());

        Random random=new Random();
        int crossPoint=random.nextInt(GAUtils.CHROMOSOME_SIZE-1);
        crossPoint++;
        for (int i = 0; i <crossPoint ; i++) {
            individual1.getChromosome()[i]=population[1].getChromosome()[i];
            individual2.getChromosome()[i]=population[0].getChromosome()[i];
        }


        //   System.out.println("crossover point "+crossPoint);
        //  System.out.println("Individual 1 "+Arrays.toString(individual1.getChromosome()));
        // System.out.println("Individual 2 "+Arrays.toString(individual2.getChromosome()));
    }
    public void showPopulation(){
        for (Individual individual:population) {
            System.out.println(Arrays.toString(individual.getChromosome())+" = "+individual.getFitness());
        }
    }
    public void sortPopulation(){
        Arrays.sort(population, Comparator.reverseOrder());
    }
    public void mutation(){
        Random random=new Random();
        // individual1
        if(random.nextDouble()>GAUtils.MUTATION_PROP){
            int index = random.nextInt(GAUtils.CHROMOSOME_SIZE);
            individual1.getChromosome()[index]=GAUtils.ALPHAS.charAt(random.nextInt(GAUtils.ALPHAS.length()));
        }
        // individual2
        if(random.nextDouble()>GAUtils.MUTATION_PROP){
            int index = random.nextInt(GAUtils.CHROMOSOME_SIZE);
            individual2.getChromosome()[index]=GAUtils.ALPHAS.charAt(random.nextInt(GAUtils.ALPHAS.length()));
        }
        // System.out.println("Après mutation ");
        // System.out.println("Individual 1 "+Arrays.toString(individual1.getChromosome()));
        //System.out.println("Individual 2 "+Arrays.toString(individual2.getChromosome()));
        individual1.calculateFintess();
        individual2.calculateFintess();
        population[GAUtils.POPULATION_SIZE-2]=individual1;
        population[GAUtils.POPULATION_SIZE-1]=individual2;


    }
    public int getBestFintness(){
        return population[0].getFitness();
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
