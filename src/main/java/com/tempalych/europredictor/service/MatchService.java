package com.tempalych.europredictor.service;

import com.tempalych.europredictor.model.dto.GroupDto;
import com.tempalych.europredictor.model.entity.Team;
import com.tempalych.europredictor.model.repository.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MatchService {

    private final TeamRepository teamRepository;

    public List<GroupDto> getGroups() {
        var groups = new ArrayList<GroupDto>();
        var teams = teamRepository.findAll();
        var id = 0L;
        for (var group: teams.stream().map(Team::getGroupName).collect(Collectors.toSet())) {
            var groupTeams = teams.stream().filter(team -> team.getGroupName().equals(group)).toList();
            var groupNameWithFlags = group + "\n" +
                    groupTeams.getFirst().getFlag() + groupTeams.get(1).getFlag() + "\n" +
                    groupTeams.get(2).getFlag() + groupTeams.get(3).getFlag();
            groups.add(new GroupDto(id, group, groupTeams, groupNameWithFlags));
            id++;
        }
        return groups;
    }
}
