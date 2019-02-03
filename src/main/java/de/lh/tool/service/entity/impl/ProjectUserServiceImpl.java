package de.lh.tool.service.entity.impl;

import org.springframework.stereotype.Service;

import de.lh.tool.domain.model.ProjectUser;
import de.lh.tool.repository.ProjectUserRepository;
import de.lh.tool.service.entity.interfaces.ProjectUserService;

@Service
public class ProjectUserServiceImpl extends BasicEntityServiceImpl<ProjectUserRepository, ProjectUser, Long>
		implements ProjectUserService {

}
