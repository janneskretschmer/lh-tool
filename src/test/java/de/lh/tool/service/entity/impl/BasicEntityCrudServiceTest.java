package de.lh.tool.service.entity.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import de.lh.tool.domain.Identifiable;
import de.lh.tool.repository.BasicEntityRepository;
import lombok.Data;
import lombok.NonNull;

public class BasicEntityCrudServiceTest {
	@Test
	public void testNullArguments() {
		TestEntityCrudService service = new TestEntityCrudService();
		assertThrows(NullPointerException.class, () -> service.checkCreatePermission(null));
		assertThrows(NullPointerException.class, () -> service.checkDeletable(null));
		assertThrows(NullPointerException.class, () -> service.checkDeletePermission(null));
		assertThrows(NullPointerException.class, () -> service.checkFindPermission((TestEntity) null));
		assertThrows(NullPointerException.class, () -> service.checkFindPermission((Long) null));
		assertThrows(NullPointerException.class, () -> service.checkReadPermission((TestEntity) null));
		assertThrows(NullPointerException.class, () -> service.checkReadPermission((Long) null));
		assertThrows(NullPointerException.class, () -> service.checkUpdatePermission(null));
		assertThrows(NullPointerException.class, () -> service.checkValidity(null));
		assertThrows(NullPointerException.class, () -> service.checkWritePermission((TestEntity) null));
		assertThrows(NullPointerException.class, () -> service.checkWritePermission((Long) null));
		assertThrows(NullPointerException.class, () -> service.convertToDto(null));
		assertEquals(0, service.convertToDtoList(null).size());
		assertThrows(NullPointerException.class, () -> service.convertToEntity(null));
		assertEquals(0, service.convertToEntityList(null).size());
		assertThrows(NullPointerException.class, () -> service.createDto(null));
		assertThrows(NullPointerException.class, () -> service.delete(null));
		assertThrows(NullPointerException.class, () -> service.deleteDtoById(null));
		assertThrows(NullPointerException.class, () -> service.filterFindResult(null));
		assertThrows(NullPointerException.class, () -> service.findDtoById(null));
		assertThrows(NullPointerException.class, () -> service.hasCreatePermission(null));
		assertThrows(NullPointerException.class, () -> service.hasDeletePermission(null));
		assertThrows(NullPointerException.class, () -> service.hasFindPermission((TestEntity) null));
		assertThrows(NullPointerException.class, () -> service.hasFindPermission((Long) null));
		assertThrows(NullPointerException.class, () -> service.hasReadPermission((TestEntity) null));
		assertThrows(NullPointerException.class, () -> service.hasReadPermission((Long) null));
		assertThrows(NullPointerException.class, () -> service.hasUpdatePermission(null));
		assertThrows(NullPointerException.class, () -> service.hasWritePermission((TestEntity) null));
		assertThrows(NullPointerException.class, () -> service.hasWritePermission((Long) null));
		assertThrows(NullPointerException.class, () -> service.postCreate(null));
		assertThrows(NullPointerException.class, () -> service.postDelete(null));
		assertThrows(NullPointerException.class, () -> service.postUpdate(null, new TestEntity()));
		assertThrows(NullPointerException.class, () -> service.postUpdate(new TestEntity(), null));
		assertThrows(NullPointerException.class, () -> service.preUpdate(null, new TestEntity()));
		assertThrows(NullPointerException.class, () -> service.preUpdate(new TestEntity(), null));
	}

	@Data
	private class TestEntity implements Identifiable<Long> {
		private Long id;
	}

	@Data
	private class TestEntityDto implements Identifiable<Long> {
		private Long id;
	}

	private class TestEntityCrudService extends
			BasicEntityCrudServiceImpl<BasicEntityRepository<TestEntity, Long>, TestEntity, TestEntityDto, Long> {

		@Override
		public boolean hasReadPermission(@NonNull TestEntity entity) {
			return false;
		}

		@Override
		public boolean hasWritePermission(@NonNull TestEntity entity) {
			return false;
		}

		@Override
		protected String getRightPrefix() {
			return "";
		}

	}
}
