package ca.mcgill.neo4j.plugins.datetime4neo;

/**
 * Copyright (c) 2014 "Mahmood S. Zargar"
 *
 * This file is a Neo4j REST API Server Plugin.
 *
 * This plugin is offered as free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

// Next steps: overlap calculator and duration calculator


import org.neo4j.graphdb.*;
import org.neo4j.server.plugins.*;
import org.neo4j.tooling.GlobalGraphOperations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Description("An extension to the Neo4j Server that lets you run simple operations on datetime fields saved as string.")
public class Dater extends ServerPlugin {
    @Name("dt_period")
    @Description("Calculates the length of a period and returns or saves the result.")
    @PluginTarget(GraphDatabaseService.class)
    public Iterable<String> dtPeriod(
            @Source GraphDatabaseService graphDb,
            @Description("The label by which nodes should be filtered.")
            @Parameter(name = "label", optional = false) String label,
            @Description("The property pointing to the beginning of the period.")
            @Parameter(name = "start", optional = false) String startProperty,
            @Description("The property pointing to the end of the period.")
            @Parameter(name = "end", optional = false) String endProperty,
            @Description("The date pattern used. (See http://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)")
            @Parameter(name = "format", optional = false) String format,
            @Description("The time unit for the results (YEARS, MONTHS, WEEKS or DAYS). Defaults to days.")
            @Parameter(name = "unit", optional = true) String unit,
            @Description("The property that must be populated with the results.")
            @Parameter(name = "output", optional = true) String outProperty
    ) {
        ArrayList<String> results = new ArrayList<>();
        Label searchLabel = DynamicLabel.label(label);
        int count = 0;

        try (Transaction tx = graphDb.beginTx()) {
            for (Node node : GlobalGraphOperations.at(graphDb).getAllNodesWithLabel(searchLabel)) {
                String start = node.getProperty(startProperty, "").toString();
                String end = node.getProperty(endProperty, "").toString();

                if (start.isEmpty() || end.isEmpty()) {
                    continue;
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

                LocalDate lsStart = LocalDate.parse(start, formatter);
                LocalDate lsEnd = LocalDate.parse(end, formatter);

                long period;

                switch (unit.toUpperCase()) {
                    case "YEARS":
                        period = ChronoUnit.YEARS.between(lsStart, lsEnd) + 1;
                        break;
                    case "MONTHS":
                        period = ChronoUnit.MONTHS.between(lsStart, lsEnd) + 1;
                        break;
                    case "WEEKS":
                        period = ChronoUnit.WEEKS.between(lsStart, lsEnd) + 1;
                        break;
                    default:
                        period = ChronoUnit.DAYS.between(lsStart, lsEnd) + 1;
                        break;
                }

                if (!(outProperty == null) && !(outProperty.isEmpty())) {
                    node.setProperty(outProperty, period);
                    count++;
                } else {
                    results.add(String.valueOf(period));
                }
            }
            tx.success();

        }
        if (count > 0) {
            results.add(count + " properties modified.");
        }
        return results;
    }


}