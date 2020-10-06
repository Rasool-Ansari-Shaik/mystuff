const equipmentMap = new Map([
    ['gentleman', 'ring'],
    ['wizard', 'staff'],
    ['archer', 'bow'],
    ['knight', 'sword'],
]);

function main(params) {
    console.log('in member_equip.js')
    // Equip the member based upon their occupation
    member = params.member;

    member.item = equipmentMap.get(member.occupation) || 'None'

    return {
        member: member
    };

}