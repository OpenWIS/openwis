package au.gov.bom.openwis.dataservices.common.domain.entity.useralarm;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarm;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmBuilder;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmCategory;
import org.openwis.dataservice.common.domain.entity.useralarm.UserAlarmReferenceType;

public class UserAlarmBuilderTest {

	@Test
	public void testUserAlarmBuilder() throws Exception {
		Date dateBeforeAlarmCreation = new Date();
		Thread.sleep(10);

		UserAlarm alarm = new UserAlarmBuilder("testUser")
							.category(UserAlarmCategory.DISSEMINATION_FAILED)
							.message("Something happened.")
							.referenceTypeKey(UserAlarmReferenceType.REQUEST, 1234)
							.getUserAlarm();

		Assert.assertNotNull(alarm);
		Assert.assertEquals("testUser", alarm.getUserId());
		Assert.assertTrue(alarm.getDateRaised().after(dateBeforeAlarmCreation));
		Assert.assertEquals(UserAlarmCategory.DISSEMINATION_FAILED, alarm.getCategory());
		Assert.assertEquals(UserAlarmReferenceType.REQUEST, alarm.getReferenceType());
		Assert.assertEquals(1234, alarm.getReferenceKey());
		Assert.assertEquals("Something happened.", alarm.getMessage());
	}
}
