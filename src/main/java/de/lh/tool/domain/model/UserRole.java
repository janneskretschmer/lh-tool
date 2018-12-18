package de.lh.tool.domain.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_role")
public class UserRole implements GrantedAuthority {

	private static final long serialVersionUID = -370455906327675533L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(insertable = true, name = "user_id", unique = true, updatable = true)
	private User user;

	@Column(name = "role", length = 100, nullable = false)
	private String role;

//  ██████╗__██████╗_██╗_____███████╗███████╗
//  ██╔══██╗██╔═══██╗██║_____██╔════╝██╔════╝
//  ██████╔╝██║___██║██║_____█████╗__███████╗
//  ██╔══██╗██║___██║██║_____██╔══╝__╚════██║
//  ██║__██║╚██████╔╝███████╗███████╗███████║
//  ╚═╝__╚═╝_╚═════╝_╚══════╝╚══════╝╚══════╝

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_CONSTRUCTION_SERVANT = "ROLE_CONSTRUCTION_SERVANT";
	public static final String ROLE_LOCAL_COORDINATOR = "ROLE_LOCAL_COORDINATOR";
	public static final String ROLE_SERVICE_COMMITTEE = "ROLE_SERVICE_COMMITTEE";
	public static final String ROLE_PUBLISHER = "ROLE_PUBLISHER";
	public static final String ROLE_STORE_KEEPER = "ROLE_STORE_KEEPER";
	public static final String ROLE_INVENTORY_MANAGER = "ROLE_INVENTORY_MANAGER";

//  ██████╗_██╗_██████╗_██╗__██╗████████╗███████╗
//  ██╔══██╗██║██╔════╝_██║__██║╚══██╔══╝██╔════╝
//  ██████╔╝██║██║__███╗███████║___██║___███████╗
//  ██╔══██╗██║██║___██║██╔══██║___██║___╚════██║
//  ██║__██║██║╚██████╔╝██║__██║___██║___███████║
//  ╚═╝__╚═╝╚═╝_╚═════╝_╚═╝__╚═╝___╚═╝___╚══════╝
//	Unfortunately granted authorities always have to start with "ROLE_". Several attempts to change that failed. 	
	public static final String RIGHT_USERS_GET_ALL = "ROLE_RIGHT_USERS_GET_ALL";
	public static final String RIGHT_USERS_CREATE = "ROLE_RIGHT_USERS_CREATE";

	private static class RoleRightManager {
		private static RoleRightManager instance;
		private final Map<String, Collection<String>> roleRights;

		private RoleRightManager() {
			roleRights = new HashMap<>();

//  _██████╗_██████╗__█████╗_███╗___██╗████████╗██╗███╗___██╗_██████╗_
//  ██╔════╝_██╔══██╗██╔══██╗████╗__██║╚══██╔══╝██║████╗__██║██╔════╝_
//  ██║__███╗██████╔╝███████║██╔██╗_██║___██║___██║██╔██╗_██║██║__███╗
//  ██║___██║██╔══██╗██╔══██║██║╚██╗██║___██║___██║██║╚██╗██║██║___██║
//  ╚██████╔╝██║__██║██║__██║██║_╚████║___██║___██║██║_╚████║╚██████╔╝
//  _╚═════╝_╚═╝__╚═╝╚═╝__╚═╝╚═╝__╚═══╝___╚═╝___╚═╝╚═╝__╚═══╝_╚═════╝_
			roleRights.put(ROLE_ADMIN, Arrays.asList(RIGHT_USERS_GET_ALL, RIGHT_USERS_CREATE));
			roleRights.put(ROLE_CONSTRUCTION_SERVANT, Arrays.asList());
			roleRights.put(ROLE_LOCAL_COORDINATOR, Arrays.asList());
			roleRights.put(ROLE_SERVICE_COMMITTEE, Arrays.asList());
			roleRights.put(ROLE_PUBLISHER, Arrays.asList());
			roleRights.put(ROLE_STORE_KEEPER, Arrays.asList());
			roleRights.put(ROLE_INVENTORY_MANAGER, Arrays.asList());
		}

		public static RoleRightManager getInstance() {
			if (instance == null) {
				instance = new RoleRightManager();
			}
			return instance;
		}

		private Collection<String> getRightsByRole(String role) {
			return roleRights.get(role);
		}

	}

	@Override
	public String getAuthority() {
		return getRole();
	}

	public Collection<GrantedAuthority> getRoleWithRights() {
		Collection<GrantedAuthority> result = new ArrayList<>();
		result.add(this);
		Collection<String> rights = RoleRightManager.getInstance().getRightsByRole(role);
		if (rights != null) {
			result.addAll(rights.stream().map(r -> new GrantedAuthority() {
				private static final long serialVersionUID = 8456596273030743217L;

				@Override
				public String getAuthority() {
					return r;
				}
			}).collect(Collectors.toList()));
		}
		return result;
	}
}