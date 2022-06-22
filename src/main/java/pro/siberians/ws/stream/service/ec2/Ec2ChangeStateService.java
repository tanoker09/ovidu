package pro.siberians.ws.stream.service.ec2;

public interface Ec2ChangeStateService {
	public void startEc2Instance(String instanceId);
	public void stopEc2Instance(String instanceId);
	public void rebootEc2Instance(String instanceId);
}
