package pro.siberians.ws.stream.service.ec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagSpecification;

public class Ec2TagSpec {
	public static TagSpecification getTagSpec(String resourceType, Map<String, String> tags) {
		final List<Tag> tagList = new ArrayList<Tag>();
		tags.forEach((key, value) -> {
			Tag tag = new Tag(key, value);
			tagList.add(tag);
		});
		return new TagSpecification().withResourceType(resourceType).withTags(tagList);
	}

}
