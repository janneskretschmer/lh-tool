package de.lh.tool.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.util.CodeGenUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_role")
@EqualsAndHashCode(of = { "role" })
@RequiredArgsConstructor
public class UserRole implements GrantedAuthority, Identifiable<Long> {

	private static final long serialVersionUID = -370455906327675533L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(insertable = true, name = "user_id", unique = true, updatable = true)
	private User user;

	@Column(name = "role", length = 100, nullable = false)
	@NonNull
	private String role;

	@Override
	public String toString() {
		return StringUtils.join(user.getFirstName(), " ", user.getLastName(), " has right ", role);
	}

//  ██████╗__██████╗_██╗_____███████╗███████╗
//  ██╔══██╗██╔═══██╗██║_____██╔════╝██╔════╝
//  ██████╔╝██║___██║██║_____█████╗__███████╗
//  ██╔══██╗██║___██║██║_____██╔══╝__╚════██║
//  ██║__██║╚██████╔╝███████╗███████╗███████║
//  ╚═╝__╚═╝_╚═════╝_╚══════╝╚══════╝╚══════╝

	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_CONSTRUCTION_SERVANT = "ROLE_CONSTRUCTION_SERVANT";
	public static final String ROLE_LOCAL_COORDINATOR = "ROLE_LOCAL_COORDINATOR";
	// needs to be able to check who will come to the construction site
	public static final String ROLE_ATTENDANCE = "ROLE_ATTENDANCE";
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

	public static final String GET_SUFFIX = "_GET";
	public static final String POST_SUFFIX = "_POST";
	public static final String PUT_SUFFIX = "_PUT";
	public static final String DELETE_SUFFIX = "_DELETE";
	public static final String CHANGE_FOREIGN_SUFFIX = "_CHANGE_FOREIGN";

	public static final String USERS_PREFIX = "ROLE_RIGHT_USERS";
	public static final String RIGHT_USERS_GET = USERS_PREFIX + GET_SUFFIX;
	public static final String RIGHT_USERS_POST = USERS_PREFIX + POST_SUFFIX;
	public static final String RIGHT_USERS_PUT = USERS_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_USERS_DELETE = USERS_PREFIX + DELETE_SUFFIX;
	public static final String RIGHT_USERS_CHANGE_FOREIGN = USERS_PREFIX + CHANGE_FOREIGN_SUFFIX;
	public static final String RIGHT_USERS_CHANGE_FOREIGN_PASSWORD = RIGHT_USERS_CHANGE_FOREIGN + "_PASSWORD";

	public static final String USERS_ROLES_PREFIX = "ROLE_RIGHT_USERS_ROLES";
	public static final String RIGHT_USERS_ROLES_GET = USERS_ROLES_PREFIX + GET_SUFFIX;
	public static final String RIGHT_USERS_ROLES_GET_FOREIGN = RIGHT_USERS_ROLES_GET + "_FOREIGN";
	public static final String RIGHT_USERS_ROLES_POST = USERS_ROLES_PREFIX + POST_SUFFIX;
	public static final String RIGHT_USERS_ROLES_DELETE = USERS_ROLES_PREFIX + DELETE_SUFFIX;

	public static final String USERS_GRANT_ROLE_PREFIX = "ROLE_RIGHT_USERS_GRANT";
	public static final String RIGHT_USERS_GRANT_ROLE_ADMIN = USERS_GRANT_ROLE_PREFIX + "_" + ROLE_ADMIN;
	public static final String RIGHT_USERS_GRANT_ROLE_CONSTRUCTION_SERVANT = USERS_GRANT_ROLE_PREFIX + "_"
			+ ROLE_CONSTRUCTION_SERVANT;
	public static final String RIGHT_USERS_GRANT_ROLE_LOCAL_COORDINATOR = USERS_GRANT_ROLE_PREFIX + "_"
			+ ROLE_LOCAL_COORDINATOR;
	public static final String RIGHT_USERS_GRANT_ROLE_ATTENDANCE = USERS_GRANT_ROLE_PREFIX + "_" + ROLE_ATTENDANCE;
	public static final String RIGHT_USERS_GRANT_ROLE_PUBLISHER = USERS_GRANT_ROLE_PREFIX + "_" + ROLE_PUBLISHER;
	public static final String RIGHT_USERS_GRANT_ROLE_STORE_KEEPER = USERS_GRANT_ROLE_PREFIX + "_" + ROLE_STORE_KEEPER;
	public static final String RIGHT_USERS_GRANT_ROLE_INVENTORY_MANAGER = USERS_GRANT_ROLE_PREFIX + "_"
			+ ROLE_INVENTORY_MANAGER;

	public static final String PROJECTS_PREFIX = "ROLE_RIGHT_PROJECTS";
	public static final String RIGHT_PROJECTS_GET = PROJECTS_PREFIX + GET_SUFFIX;
	public static final String RIGHT_PROJECTS_POST = PROJECTS_PREFIX + POST_SUFFIX;
	public static final String RIGHT_PROJECTS_PUT = PROJECTS_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_PROJECTS_DELETE = PROJECTS_PREFIX + DELETE_SUFFIX;
	public static final String RIGHT_PROJECTS_CHANGE_FOREIGN = PROJECTS_PREFIX + CHANGE_FOREIGN_SUFFIX;

	public static final String PROJECTS_USERS_PREFIX = "ROLE_RIGHT_PROJECTS_USERS";
	public static final String RIGHT_PROJECTS_USERS_GET = PROJECTS_USERS_PREFIX + GET_SUFFIX;
	public static final String RIGHT_PROJECTS_USERS_POST = PROJECTS_USERS_PREFIX + POST_SUFFIX;
	public static final String RIGHT_PROJECTS_USERS_DELETE = PROJECTS_USERS_PREFIX + DELETE_SUFFIX;
	public static final String RIGHT_PROJECTS_USERS_CHANGE_FOREIGN = PROJECTS_USERS_PREFIX + CHANGE_FOREIGN_SUFFIX;

	public static final String PROJECTS_HELPER_TYPES_PREFIX = "ROLE_RIGHT_PROJECTS_HELPER_TYPES";
	public static final String RIGHT_PROJECTS_HELPER_TYPES_GET = PROJECTS_HELPER_TYPES_PREFIX + GET_SUFFIX;
	public static final String RIGHT_PROJECTS_HELPER_TYPES_POST = PROJECTS_HELPER_TYPES_PREFIX + POST_SUFFIX;
	public static final String RIGHT_PROJECTS_HELPER_TYPES_PUT = PROJECTS_HELPER_TYPES_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_PROJECTS_HELPER_TYPES_DELETE = PROJECTS_HELPER_TYPES_PREFIX + DELETE_SUFFIX;

	public static final String HELPER_TYPES_PREFIX = "ROLE_RIGHT_HELPER_TYPES";
	public static final String RIGHT_HELPER_TYPES_GET = HELPER_TYPES_PREFIX + GET_SUFFIX;
	public static final String RIGHT_HELPER_TYPES_POST = HELPER_TYPES_PREFIX + POST_SUFFIX;
	public static final String RIGHT_HELPER_TYPES_PUT = HELPER_TYPES_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_HELPER_TYPES_DELETE = HELPER_TYPES_PREFIX + DELETE_SUFFIX;

	public static final String NEEDS_PREFIX = "ROLE_RIGHT_NEEDS";
	public static final String RIGHT_NEEDS_GET = NEEDS_PREFIX + GET_SUFFIX;
	public static final String RIGHT_NEEDS_POST = NEEDS_PREFIX + POST_SUFFIX;
	public static final String RIGHT_NEEDS_PUT = NEEDS_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_NEEDS_DELETE = NEEDS_PREFIX + DELETE_SUFFIX;
	public static final String RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT = NEEDS_PREFIX + CHANGE_FOREIGN_SUFFIX + "_PROJECT";
	public static final String RIGHT_NEEDS_CHANGE_FOREIGN_USER = NEEDS_PREFIX + CHANGE_FOREIGN_SUFFIX + "_USER";
	public static final String RIGHT_NEEDS_GET_FOREIGN_USER = NEEDS_PREFIX + GET_SUFFIX + "_FOREIGN_USER";
	public static final String RIGHT_NEEDS_GET_ANONYMIZED_USER_LIST = NEEDS_PREFIX + GET_SUFFIX
			+ "_ANONYMIZED_USER_LIST";
	public static final String RIGHT_NEEDS_APPLY = NEEDS_PREFIX + "_APPLY";
	public static final String RIGHT_NEEDS_APPROVE = NEEDS_PREFIX + "_APPROVE";
	public static final String RIGHT_NEEDS_VIEW_APPROVED = NEEDS_PREFIX + "_VIEW_APPROVED";

	public static final String NEEDS_USERS_PREFIX = "ROLE_RIGHT_NEEDS_USERS";
	public static final String RIGHT_NEEDS_USERS_PUT = NEEDS_USERS_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_NEEDS_USERS_GET = NEEDS_USERS_PREFIX + GET_SUFFIX;

	public static final String ITEMS_PREFIX = "ROLE_RIGHT_ITEMS";
	public static final String RIGHT_ITEMS_GET = ITEMS_PREFIX + GET_SUFFIX;
	public static final String RIGHT_ITEMS_POST = ITEMS_PREFIX + POST_SUFFIX;
	public static final String RIGHT_ITEMS_PUT = ITEMS_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_ITEMS_PATCH = ITEMS_PREFIX + "_PATCH";
	public static final String RIGHT_ITEMS_PATCH_BROKEN = RIGHT_ITEMS_PATCH + "_BROKEN";
	public static final String RIGHT_ITEMS_PATCH_SLOT = RIGHT_ITEMS_PATCH + "_SLOT";
	public static final String RIGHT_ITEMS_PATCH_QUANTITY = RIGHT_ITEMS_PATCH + "_QUANTITY";
	public static final String RIGHT_ITEMS_DELETE = ITEMS_PREFIX + DELETE_SUFFIX;
	public static final String RIGHT_ITEMS_GET_FOREIGN_PROJECT = RIGHT_ITEMS_GET + "_FOREIGN_PROJECT";

	public static final String ITEMS_NOTES_PREFIX = "ROLE_RIGHT_ITEMS_NOTES";
	public static final String RIGHT_ITEMS_NOTES_GET = ITEMS_NOTES_PREFIX + GET_SUFFIX;
	public static final String RIGHT_ITEMS_NOTES_POST = ITEMS_NOTES_PREFIX + POST_SUFFIX;
	public static final String RIGHT_ITEMS_NOTES_DELETE = ITEMS_NOTES_PREFIX + DELETE_SUFFIX;
	public static final String RIGHT_ITEMS_NOTES_DELETE_FOREIGN = RIGHT_ITEMS_NOTES_DELETE + "_FOREIGN";

	public static final String ITEM_TAGS_PREFIX = "ROLE_RIGHT_ITEM_TAGS";
	public static final String RIGHT_ITEM_TAGS_GET = ITEM_TAGS_PREFIX + GET_SUFFIX;
	public static final String RIGHT_ITEM_TAGS_POST = ITEM_TAGS_PREFIX + POST_SUFFIX;
	public static final String RIGHT_ITEM_TAGS_DELETE = ITEM_TAGS_PREFIX + DELETE_SUFFIX;

	public static final String ITEMS_ITEM_TAGS_PREFIX = "ROLE_RIGHT_ITEMS_ITEM_TAGS";
	public static final String RIGHT_ITEMS_ITEM_TAGS_GET = ITEMS_ITEM_TAGS_PREFIX + GET_SUFFIX;
	public static final String RIGHT_ITEMS_ITEM_TAGS_POST = ITEMS_ITEM_TAGS_PREFIX + POST_SUFFIX;
	public static final String RIGHT_ITEMS_ITEM_TAGS_DELETE = ITEMS_ITEM_TAGS_PREFIX + DELETE_SUFFIX;

	public static final String SLOTS_PREFIX = "ROLE_RIGHT_SLOTS";
	public static final String RIGHT_SLOTS_GET = SLOTS_PREFIX + GET_SUFFIX;
	public static final String RIGHT_SLOTS_POST = SLOTS_PREFIX + POST_SUFFIX;
	public static final String RIGHT_SLOTS_PUT = SLOTS_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_SLOTS_DELETE = SLOTS_PREFIX + DELETE_SUFFIX;

	public static final String STORES_PREFIX = "ROLE_RIGHT_STORES";
	public static final String RIGHT_STORES_GET = STORES_PREFIX + GET_SUFFIX;
	public static final String RIGHT_STORES_POST = STORES_PREFIX + POST_SUFFIX;
	public static final String RIGHT_STORES_PUT = STORES_PREFIX + PUT_SUFFIX;
	public static final String RIGHT_STORES_DELETE = STORES_PREFIX + DELETE_SUFFIX;
	public static final String RIGHT_STORES_GET_FOREIGN_PROJECT = RIGHT_STORES_GET + "_FOREIGN_PROJECT";

	public static final String TECHNICAL_CREWS_PREFIX = "ROLE_RIGHT_TECHNICAL_CREWS";
	public static final String RIGHT_TECHNICAL_CREWS_GET = TECHNICAL_CREWS_PREFIX + GET_SUFFIX;

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

			roleRights.put(ROLE_ADMIN, List.of(//
					RIGHT_USERS_GET, RIGHT_USERS_POST, RIGHT_USERS_PUT, RIGHT_USERS_DELETE, RIGHT_USERS_CHANGE_FOREIGN,
					RIGHT_USERS_CHANGE_FOREIGN_PASSWORD,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_USERS_ROLES_GET, RIGHT_USERS_ROLES_GET_FOREIGN, RIGHT_USERS_ROLES_POST,
					RIGHT_USERS_ROLES_DELETE, RIGHT_USERS_GRANT_ROLE_ADMIN, RIGHT_USERS_GRANT_ROLE_CONSTRUCTION_SERVANT,
					RIGHT_USERS_GRANT_ROLE_INVENTORY_MANAGER, RIGHT_USERS_GRANT_ROLE_LOCAL_COORDINATOR,
					RIGHT_USERS_GRANT_ROLE_ATTENDANCE, RIGHT_USERS_GRANT_ROLE_PUBLISHER,
					RIGHT_USERS_GRANT_ROLE_STORE_KEEPER,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_PROJECTS_GET, RIGHT_PROJECTS_POST, RIGHT_PROJECTS_DELETE, RIGHT_PROJECTS_PUT,
					RIGHT_PROJECTS_CHANGE_FOREIGN, RIGHT_PROJECTS_USERS_GET, RIGHT_PROJECTS_USERS_DELETE,
					RIGHT_PROJECTS_USERS_POST, RIGHT_PROJECTS_USERS_CHANGE_FOREIGN, RIGHT_PROJECTS_HELPER_TYPES_GET,
					RIGHT_PROJECTS_HELPER_TYPES_POST, RIGHT_PROJECTS_HELPER_TYPES_PUT,
					RIGHT_PROJECTS_HELPER_TYPES_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_HELPER_TYPES_GET, RIGHT_HELPER_TYPES_POST, RIGHT_HELPER_TYPES_PUT, RIGHT_HELPER_TYPES_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_NEEDS_GET, RIGHT_NEEDS_POST, RIGHT_NEEDS_PUT, RIGHT_NEEDS_DELETE,
					RIGHT_NEEDS_CHANGE_FOREIGN_PROJECT, RIGHT_NEEDS_CHANGE_FOREIGN_USER, RIGHT_NEEDS_GET_FOREIGN_USER,
					RIGHT_NEEDS_GET_ANONYMIZED_USER_LIST, RIGHT_NEEDS_USERS_PUT, RIGHT_NEEDS_USERS_GET,
					RIGHT_NEEDS_APPLY, RIGHT_NEEDS_APPROVE, RIGHT_NEEDS_VIEW_APPROVED,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_GET, RIGHT_ITEMS_GET_FOREIGN_PROJECT, RIGHT_ITEMS_POST, RIGHT_ITEMS_PUT,
					RIGHT_ITEMS_PATCH, RIGHT_ITEMS_PATCH_BROKEN, RIGHT_ITEMS_PATCH_SLOT, RIGHT_ITEMS_PATCH_QUANTITY,
					RIGHT_ITEM_TAGS_GET, RIGHT_ITEM_TAGS_POST, RIGHT_ITEM_TAGS_DELETE, RIGHT_ITEMS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_ITEM_TAGS_GET, RIGHT_ITEMS_ITEM_TAGS_POST, RIGHT_ITEMS_ITEM_TAGS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_NOTES_GET, RIGHT_ITEMS_NOTES_POST, RIGHT_ITEMS_NOTES_DELETE,
					RIGHT_ITEMS_NOTES_DELETE_FOREIGN,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_SLOTS_GET, RIGHT_SLOTS_POST, RIGHT_SLOTS_PUT, RIGHT_SLOTS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_STORES_GET, RIGHT_STORES_GET_FOREIGN_PROJECT, RIGHT_STORES_POST, RIGHT_STORES_PUT,
					RIGHT_STORES_DELETE, RIGHT_SLOTS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_TECHNICAL_CREWS_GET));

			roleRights.put(ROLE_CONSTRUCTION_SERVANT, List.of(RIGHT_USERS_GET, RIGHT_USERS_POST, RIGHT_USERS_PUT,
					RIGHT_USERS_DELETE, RIGHT_USERS_CHANGE_FOREIGN, RIGHT_PROJECTS_GET, RIGHT_PROJECTS_POST,
					RIGHT_PROJECTS_PUT, RIGHT_PROJECTS_USERS_GET, RIGHT_PROJECTS_USERS_DELETE,
					RIGHT_PROJECTS_USERS_POST, RIGHT_PROJECTS_HELPER_TYPES_GET, RIGHT_PROJECTS_HELPER_TYPES_POST,
					RIGHT_PROJECTS_HELPER_TYPES_PUT, RIGHT_PROJECTS_HELPER_TYPES_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_USERS_ROLES_GET, RIGHT_USERS_ROLES_POST, RIGHT_USERS_ROLES_DELETE,
					RIGHT_USERS_GRANT_ROLE_CONSTRUCTION_SERVANT, RIGHT_USERS_GRANT_ROLE_INVENTORY_MANAGER,
					RIGHT_USERS_GRANT_ROLE_LOCAL_COORDINATOR, RIGHT_USERS_GRANT_ROLE_ATTENDANCE,
					RIGHT_USERS_GRANT_ROLE_PUBLISHER, RIGHT_USERS_GRANT_ROLE_STORE_KEEPER,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_HELPER_TYPES_GET,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_NEEDS_GET, RIGHT_NEEDS_POST, RIGHT_NEEDS_PUT, RIGHT_NEEDS_DELETE,
					RIGHT_NEEDS_CHANGE_FOREIGN_USER, RIGHT_NEEDS_GET_FOREIGN_USER, RIGHT_NEEDS_GET_ANONYMIZED_USER_LIST,
					RIGHT_NEEDS_USERS_PUT, RIGHT_NEEDS_USERS_GET, RIGHT_NEEDS_APPROVE, RIGHT_NEEDS_VIEW_APPROVED,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_GET, RIGHT_ITEMS_GET_FOREIGN_PROJECT, RIGHT_ITEMS_POST, RIGHT_ITEMS_PUT,
					RIGHT_ITEMS_PATCH, RIGHT_ITEMS_PATCH_BROKEN, RIGHT_ITEMS_PATCH_SLOT, RIGHT_ITEMS_PATCH_QUANTITY,
					RIGHT_ITEM_TAGS_GET, RIGHT_ITEM_TAGS_POST, RIGHT_ITEM_TAGS_DELETE, RIGHT_ITEMS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_ITEM_TAGS_GET, RIGHT_ITEMS_ITEM_TAGS_POST, RIGHT_ITEMS_ITEM_TAGS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_NOTES_GET, RIGHT_ITEMS_NOTES_POST, RIGHT_ITEMS_NOTES_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_SLOTS_GET, RIGHT_SLOTS_POST, RIGHT_SLOTS_PUT, RIGHT_SLOTS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_STORES_GET, RIGHT_STORES_GET_FOREIGN_PROJECT, RIGHT_STORES_POST, RIGHT_STORES_PUT,
					RIGHT_STORES_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_TECHNICAL_CREWS_GET));

			roleRights.put(ROLE_LOCAL_COORDINATOR,
					List.of(RIGHT_USERS_GET, RIGHT_USERS_POST, RIGHT_USERS_PUT, RIGHT_USERS_DELETE,
							RIGHT_USERS_CHANGE_FOREIGN,
							// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
							RIGHT_USERS_ROLES_GET, RIGHT_USERS_ROLES_POST, RIGHT_USERS_ROLES_DELETE,
							RIGHT_USERS_GRANT_ROLE_PUBLISHER, RIGHT_USERS_GRANT_ROLE_STORE_KEEPER,
							// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
							RIGHT_PROJECTS_GET, RIGHT_PROJECTS_USERS_GET, RIGHT_PROJECTS_USERS_POST,
							RIGHT_PROJECTS_HELPER_TYPES_GET,
							// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
							RIGHT_HELPER_TYPES_GET, RIGHT_NEEDS_POST, RIGHT_NEEDS_PUT, RIGHT_NEEDS_GET,
							RIGHT_NEEDS_DELETE, RIGHT_NEEDS_CHANGE_FOREIGN_USER, RIGHT_NEEDS_GET_FOREIGN_USER,
							RIGHT_NEEDS_GET_ANONYMIZED_USER_LIST, RIGHT_NEEDS_USERS_GET, RIGHT_NEEDS_USERS_PUT,
							RIGHT_NEEDS_APPLY, RIGHT_NEEDS_APPROVE, RIGHT_NEEDS_VIEW_APPROVED));

			roleRights.put(ROLE_ATTENDANCE,
					List.of(RIGHT_USERS_GET, RIGHT_USERS_PUT, RIGHT_PROJECTS_GET, RIGHT_PROJECTS_HELPER_TYPES_GET,
							RIGHT_HELPER_TYPES_GET, RIGHT_NEEDS_GET, RIGHT_NEEDS_USERS_GET, RIGHT_NEEDS_USERS_PUT,
							RIGHT_NEEDS_GET_FOREIGN_USER, RIGHT_NEEDS_GET_ANONYMIZED_USER_LIST, RIGHT_NEEDS_APPLY,
							RIGHT_NEEDS_VIEW_APPROVED));

			roleRights.put(ROLE_PUBLISHER,
					List.of(RIGHT_USERS_GET, RIGHT_USERS_PUT, RIGHT_PROJECTS_GET, RIGHT_PROJECTS_HELPER_TYPES_GET,
							RIGHT_HELPER_TYPES_GET, RIGHT_NEEDS_USERS_GET, RIGHT_NEEDS_USERS_PUT, RIGHT_NEEDS_APPLY,
							RIGHT_NEEDS_GET, RIGHT_NEEDS_GET_ANONYMIZED_USER_LIST));
			roleRights.put(ROLE_STORE_KEEPER, List.of(RIGHT_USERS_GET, RIGHT_USERS_PUT,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_SLOTS_GET,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_STORES_GET,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_GET, RIGHT_ITEMS_ITEM_TAGS_GET, RIGHT_ITEMS_NOTES_GET, RIGHT_ITEMS_PATCH,
					RIGHT_ITEMS_PATCH_BROKEN, RIGHT_ITEMS_PATCH_QUANTITY, RIGHT_ITEMS_NOTES_POST,
					RIGHT_ITEMS_NOTES_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_TECHNICAL_CREWS_GET));
			roleRights.put(ROLE_INVENTORY_MANAGER, List.of(RIGHT_USERS_GET, RIGHT_USERS_PUT,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_GET, RIGHT_ITEMS_GET_FOREIGN_PROJECT, RIGHT_ITEMS_POST, RIGHT_ITEMS_PUT,
					RIGHT_ITEMS_PATCH, RIGHT_ITEMS_PATCH_BROKEN, RIGHT_ITEMS_PATCH_SLOT, RIGHT_ITEMS_PATCH_QUANTITY,
					RIGHT_ITEM_TAGS_GET, RIGHT_ITEM_TAGS_POST, RIGHT_ITEM_TAGS_DELETE, RIGHT_ITEMS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_ITEM_TAGS_GET, RIGHT_ITEMS_ITEM_TAGS_POST, RIGHT_ITEMS_ITEM_TAGS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_ITEMS_NOTES_GET, RIGHT_ITEMS_NOTES_POST, RIGHT_ITEMS_NOTES_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_SLOTS_GET, RIGHT_SLOTS_POST, RIGHT_SLOTS_PUT, RIGHT_SLOTS_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_STORES_GET, RIGHT_STORES_GET_FOREIGN_PROJECT, RIGHT_STORES_POST, RIGHT_STORES_PUT,
					RIGHT_STORES_DELETE,
					// \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\ // \\
					RIGHT_TECHNICAL_CREWS_GET));
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

		private Set<String> getRoles() {
			return roleRights.keySet();
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

	public static Set<String> getRoles() {
		return RoleRightManager.getInstance().getRoles();
	}

	// needs to be executed after rights or roles have changed
	public static void main(String... options) {
		CodeGenUtil.generateJavascriptWithConstants(UserRole.class, "permissions.js");
	}

}
