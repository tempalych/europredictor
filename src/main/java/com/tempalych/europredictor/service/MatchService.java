package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.dto.GroupDto;
import com.tempalych.europredictor.model.entity.Match;
import com.tempalych.europredictor.model.entity.Team;
import com.tempalych.europredictor.model.repository.MatchRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

    public List<GroupDto> getGroups() {
        var groups = new ArrayList<GroupDto>();
        var matches = matchRepository.findAll();
        var id = 0L;
        for (var group: matches.stream().map(Match::getGroupName).collect(Collectors.toSet())) {
            var groupMatches = matches.stream().filter(match -> match.getGroupName().equals(group)).collect(Collectors.toSet());
            var groupTeams = new HashSet<Team>();
            for (var match: groupMatches) {
                groupTeams.add(match.getHomeTeam());
                groupTeams.add(match.getVisitorTeam());
            }

            StringBuilder groupNameWithFlags = new StringBuilder(group).append("\n");
            var groupTeamsList = groupTeams.stream().sorted(Comparator.comparingLong(Team::getId)).toList();

            if (groupTeamsList.size() == 4) {
                boolean addLineBreak = false;
                for (var team : groupTeamsList) {
                    groupNameWithFlags.append(team.getFlag());
                    if (addLineBreak) {
                        groupNameWithFlags.append("\n");
                    }
                    addLineBreak = !addLineBreak;
                }
            } else {
                groupNameWithFlags.append("🇪🇺");
            }

            groups.add(new GroupDto(id, group, groupTeamsList, groupNameWithFlags.toString()));
            id++;
        }
        return groups;
    }
}
