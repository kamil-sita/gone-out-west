package pl.sita.goneoutwest;

import java.util.*;
import java.util.stream.Collectors;

public class DistanceCalculator {

    public static int letterWorth(Town town, List<Connection> connections, ColorType colorType) {
        Queue<TownAndCost> toVisit = new PriorityQueue<>();
        toVisit.add(new TownAndCost(town, 0));

        int worth = letterWorth(connections, colorType, new HashSet<>(), toVisit);
        return (worth - 1) * 2 + 1;
    }

    private static int letterWorth(List<Connection> connections, ColorType colorType, Set<Town> visited, Queue<TownAndCost> toVisit) {
        TownAndCost townAndCost = toVisit.remove();

        Town town = townAndCost.town;
        if (visited.contains(town)) {
            return letterWorth(connections, colorType, visited, toVisit);
        }
        visited.add(town);
        if (town.getTownColor() == colorType) {
            return townAndCost.cost;
        }

        List<Connection> thisTownConnections = connections.stream()
                .filter(connection -> connection.contains(town))
                .collect(Collectors.toList());

        List<Town> connectedTowns = thisTownConnections.stream()
                .map(connection -> {
                    if (connection.getTown1() == town) {
                        return connection.getTown2();
                    } else {
                        return connection.getTown1();
                    }
                }).collect(Collectors.toList());

        for (Town connectedTown : connectedTowns) {
            toVisit.add(new TownAndCost(connectedTown, townAndCost.cost + 1));
        }

        return letterWorth(connections, colorType, visited, toVisit);
    }

    private static class TownAndCost implements Comparable<TownAndCost> {
        private Town town;
        private int cost;

        public TownAndCost(Town town, int cost) {
            this.town = town;
            this.cost = cost;
        }

        @Override
        public int compareTo(TownAndCost o) {
            return Integer.compare(cost, o.cost);
        }
    }

}
